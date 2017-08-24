package com.codebroker.core.actor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.codebroker.api.CodeBrokerAppListener;
import com.codebroker.core.ContextResolver;
import com.codebroker.core.ServerEngine;
import com.codebroker.core.cluster.ClusterDistributedPub;
import com.codebroker.core.entities.User;
import com.codebroker.core.manager.AkkaBootService;
import com.codebroker.core.manager.AreaManager;
import com.codebroker.core.manager.UserManager;
import com.codebroker.exception.NoAuthException;
import com.codebroker.util.AkkaMediator;
import com.google.common.collect.Lists;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;

/**
 * 顶级游戏世界 一个JVM仅有此一个 下级 {@code IArea} 管理器 {@code IAreaManager} 下级{@code IGrid}}
 * 管理器{@code IArea}
 * 
 * @author server
 *
 */
public class WorldActor extends AbstractActor {

	public static final String IDENTIFY = WorldActor.class.getSimpleName().toString();
	/**
	 * 保存服务器WorldActor地址的
	 * akka.tcp://CodeBroker@192.168.0.127:2551/user/WorldActor
	 */
	private Map<Integer, String> serverIdMap = new HashMap<Integer, String>();

	private ActorRef areaManagerRef;
	private ActorRef userManagerRef;
	
	private List<ActorRef> areaList=Lists.newArrayList();
	
	private List<ActorRef> gridList=Lists.newArrayList();

	private static Logger logger = LoggerFactory.getLogger("WorldActor");

	Map<String, ActorRef> gmSession = new ConcurrentHashMap<String, ActorRef>();

	/**
	 * 处理用户从新连接
	 * 
	 * @param actorRef
	 * 
	 * @param msg
	 */
	private void processReconnection(String key, ActorRef actorRef) {
		UserManagerActor.FindUserByRebindKey message = new UserManagerActor.FindUserByRebindKey(key);
		userManagerRef.tell(message, actorRef);
	}

	private void userConnect2Server(String name, String parms, ActorRef actorRef) {
		// 验证登入
		CodeBrokerAppListener appListener = ContextResolver.getAppListener();
		try {
			String handleLogin;
			if (appListener != null) {
				handleLogin = appListener.handleLogin(name, parms);
			} else {
				handleLogin = "Test1234";
			}
			User user = null;
			try {
				user = AkkaMediator.getCallBak(userManagerRef,
						new UserManagerActor.CreateUserWithSession(actorRef, handleLogin));
			} catch (Exception e) {
				getSender().tell(new SessionActor.UserConnect2Server(false), getSelf());
				e.printStackTrace();
			}
			getSender().tell(new SessionActor.UserConnect2Server(true), user.getActorRef());
		} catch (NoAuthException e1) {
			getSender().tell(new SessionActor.UserConnect2Server(false), getSelf());
		}
	}

	@Override
	public Receive createReceive() {
		return ReceiveBuilder.create()
		  .match(UserConnect2Server.class, msg -> {
			userConnect2Server(msg.name, msg.params, getSender());
		}).match(UserReconnectionTry.class, msg -> {
			processReconnection(msg.reBindKey, getSender());
		}).match(CreateArea.class, msg -> {
			areaManagerRef.tell(new AreaManagerActor.CreateArea(msg.areaId), getSender());
		}).match(Initialize.class, msg -> {
			initialize();
		}).match(String.class, msg -> {
			processStringMessage(msg);
		}).match(HelloWorld.class, msg -> {
			System.err.println(getSender().path().toString());
		}).match(NewServerComeIn.class, msg -> {
			processNewCome(msg);
		}).match(HandShake.class, msg -> {
			processShank(msg);
		}).matchAny(o -> logger.info("received unknown message {}", o)).build();
	}

	private void processShank(HandShake msg) {
		// akka.tcp://CodeBroker@192.168.0.202:2551/
		if (msg.serverId != ServerEngine.serverId) {
			areaManagerRef.tell(new AreaManagerActor.NewServerComeIn(msg.serverId, getSender().path().toString()),
					getSelf());
		}
		serverIdMap.put(msg.serverId, getSender().path().parent().toString());
		// akka.tcp://CodeBroker@192.168.0.127:2551/user/WorldActor
		logger.info("get HandShaske {} path {} ", msg.serverId, getSender().path().parent().toString());
	}

	private void processNewCome(NewServerComeIn msg) {
		// msg.remotePath=akka.tcp://CodeBroker@192.168.0.127:25514
		String fixSupervisorPath = AkkaMediator.getFixSupervisorPath(msg.remotePath, WorldActor.IDENTIFY);
		ActorSelection remoteActorSelection = AkkaMediator.getRemoteActorSelection(fixSupervisorPath);
		remoteActorSelection.tell(new WorldActor.HandShake(ServerEngine.serverId), getSelf());
	}

	private void processStringMessage(String msg) {
		logger.info("get Message {}", msg);
		/**
		 * 处理GMsession
		 */
		JSONObject json = JSON.parseObject(msg);
		ActorRef actorRef = null;
		String COMMAND_ID = json.getString("id");
		if (gmSession.containsKey(COMMAND_ID)) {
			actorRef = gmSession.get(COMMAND_ID);
		} else {
			// actorRef =
			// getContext().actorOf(Props.create(GMSessionActor.class,
			// getSender()), COMMAND_ID);
			// SessionActor.UserConnect2Server connect2Server=new
			// SessionActor.UserConnect2Server(true);
			// actorRef.tell(connect2Server, getSender());
			gmSession.put(COMMAND_ID, actorRef);
		}

		AkkaBootService component = ContextResolver.getComponent(AkkaBootService.class);
		ActorRef localPath = component.getLocalPath(ClusterDistributedPub.IDENTIFY);
		localPath.tell("ssss", ActorRef.noSender());

		actorRef.tell(msg, getSender());
	}

	private void initialize() {
		logger.debug("initialize Game word need start");
		/**
		 * 初始化空间管理器 akka://CodeBroker/user/WorldActor/AreaManagerActor
		 */
		areaManagerRef = getContext().actorOf(Props.create(AreaManagerActor.class,getSelf()), AreaManagerActor.IDENTIFY);
		AreaManager gridLeaderProxy = new AreaManager(areaManagerRef);
		AkkaBootService component = ContextResolver.getComponent(AkkaBootService.class);
		component.setGridLeader(gridLeaderProxy);
		getContext().watch(areaManagerRef);
		logger.info("AreaManager Path= {}", areaManagerRef.path().toString());
		/**
		 * 用户管理器 akka://CodeBroker/user/WorldActor/UserManagerActor
		 */
		UserManager manager = new UserManager();
		userManagerRef = getContext().actorOf(Props.create(UserManagerActor.class, manager), UserManagerActor.IDENTIFY);
		manager.setManagerRef(userManagerRef);
		getContext().watch(userManagerRef);
		component.setUserManager(manager);
		logger.info("UserManager Path= {}", userManagerRef.path().toString());
	}

	public static class Initialize {
	}

	public static class UserReconnectionTry {
		public final String reBindKey;

		public UserReconnectionTry(String reBindKey) {
			super();
			this.reBindKey = reBindKey;
		}

	}

	public static class CreateArea {
		public final int areaId;

		public CreateArea(int areaId) {
			super();
			this.areaId = areaId;
		}

	}

	public static class UserConnect2Server {
		public final String name;
		public final String params;

		public UserConnect2Server(String name, String params) {
			super();
			this.name = name;
			this.params = params;
		}

	}

	public static class HelloWorld implements Serializable {

		private static final long serialVersionUID = -3459373230872074079L;

		public final int selfServerId;

		public HelloWorld(int selfServerId) {
			super();
			this.selfServerId = selfServerId;
		}
	}

	public static class NewServerComeIn {

		public final long serverUId;
		public final String remotePath;

		public NewServerComeIn(long serverUId, String remotePath) {
			super();
			this.serverUId = serverUId;
			this.remotePath = remotePath;
		}
	}

	public static class RemoveServer {

		public final long serverUId;

		public RemoveServer(long serverUId) {
			super();
			this.serverUId = serverUId;
		}

	}

	public static class HandShake implements Serializable {

		private static final long serialVersionUID = -5240650967054945320L;

		public final int serverId;

		public HandShake(int serverId) {
			super();
			this.serverId = serverId;
		}

	}
}
