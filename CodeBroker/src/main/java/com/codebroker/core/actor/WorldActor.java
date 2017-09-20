package com.codebroker.core.actor;

import java.io.Serializable;
import java.util.HashMap;
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
import com.codebroker.core.local.WorldCreateNPC;
import com.codebroker.core.manager.AkkaBootService;
import com.codebroker.core.manager.AreaManager;
import com.codebroker.core.manager.UserManager;
import com.codebroker.protocol.ThriftSerializerFactory;
import com.codebroker.util.AkkaMediator;
import com.message.thrift.actor.ActorMessage;
import com.message.thrift.actor.Operation;
import com.message.thrift.actor.areamanager.CreateArea;
import com.message.thrift.actor.usermanager.CreateUserWithSession;
import com.message.thrift.actor.world.HandShake;
import com.message.thrift.actor.world.NewServerComeIn;
import com.message.thrift.actor.world.UserConnect2World;
import com.message.thrift.actor.world.UserReconnectionTry;

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
	ThriftSerializerFactory thriftSerializerFactory=new ThriftSerializerFactory();
	public static final String USER_PRFIX = "USER-";
	public static final String NPC_PRFIX = "NPC-";
	
	public static final String IDENTIFY = WorldActor.class.getSimpleName().toString();
	/**
	 * 保存服务器WorldActor地址的
	 * akka.tcp://CodeBroker@192.168.0.127:2551/user/WorldActor
	 */
	private Map<Integer, String> serverIdMap = new HashMap<Integer, String>();

	//区域管理器的地址引用
	private ActorRef areaManagerRef;
	//用户管理器的地址引用
	private ActorRef userManagerRef;
	//NPC用户管理器的地址引用
	private ActorRef npcManagerRef;

	private static Logger logger = LoggerFactory.getLogger("WorldActor");

	Map<String, ActorRef> gmSession = new ConcurrentHashMap<String, ActorRef>();

	@Override
	public Receive createReceive() {	
		return ReceiveBuilder.create()
			.match(byte[].class, msg -> {
			ActorMessage actorMessage = thriftSerializerFactory.getActorMessage(msg);
			switch (actorMessage.op) {
			case WORLD_INITIALIZE:
				initialize();
				break;
			case WORLD_USER_CONNECT_2_WORLD:
				UserConnect2World userConnect2World = new UserConnect2World();
				thriftSerializerFactory.deserialize(userConnect2World, actorMessage.messageRaw);
				userConnect2Server(userConnect2World.name, userConnect2World.params, getSender());
				break;
			case WORLD_USER_RECONNECTION_TRY:
				UserReconnectionTry reconnectionTry = new UserReconnectionTry();
				thriftSerializerFactory.deserialize(reconnectionTry, actorMessage.messageRaw);
				processReconnection(reconnectionTry.reBindKey, getSender());
				break;
			case AREA_MANAGER_CREATE_AREA:
				CreateArea createArea = new CreateArea();
				byte[] actorMessageWithSubClass = thriftSerializerFactory
						.getActorMessageWithSubClass(Operation.AREA_MANAGER_CREATE_AREA, createArea);
				areaManagerRef.tell(actorMessageWithSubClass, getSender());
				break;
			case WORLD_NER_SERVER_COMING:
				NewServerComeIn newServerComeIn=new NewServerComeIn();
				thriftSerializerFactory.deserialize(newServerComeIn, actorMessage.messageRaw);
				processNewCome(newServerComeIn.serverUId,newServerComeIn.remotePath);
				break;
			case WORLD_HAND_SHAKE:
				HandShake handShake=new HandShake();
				thriftSerializerFactory.deserialize(handShake, actorMessage.messageRaw);
				processShank(handShake.serverId,handShake.serverUid);
				break;
			default:
				break;
			}
		})
		//转发给用户管理器
		.match(WorldCreateNPC.class, msg->{
			userManagerRef.tell(msg, getSelf());
		})
		
		.match(String.class, msg -> {
			processStringMessage(msg);
		}).match(HelloWorld.class, msg -> {
			System.err.println(getSender().path().toString());
		})
		 .matchAny(o -> logger.info("received unknown message {}", o))
		 .build();
	}
	
	private void initialize() {
		logger.debug("initialize Game word need start");
		AkkaBootService component = ContextResolver.getComponent(AkkaBootService.class);
		/**
		 * 用户管理器 akka://CodeBroker/user/WorldActor/UserManagerActor
		 */
		UserManager manager = new UserManager();
		userManagerRef = getContext().actorOf(Props.create(UserManagerActor.class, getSelf()),
				UserManagerActor.IDENTIFY);
		manager.setManagerRef(userManagerRef);
		getContext().watch(userManagerRef);
		component.setUserManager(manager);
		logger.info("UserManager Path= {}", userManagerRef.path().toString());
		
		/**
		 * 初始化空间管理器 akka://CodeBroker/user/WorldActor/AreaManagerActor
		 */
		areaManagerRef = getContext().actorOf(Props.create(AreaManagerActor.class, getSelf(),userManagerRef),
				AreaManagerActor.IDENTIFY);
		AreaManager gridLeaderProxy = new AreaManager(areaManagerRef);
		
		component.setGridLeader(gridLeaderProxy);
		getContext().watch(areaManagerRef);
		logger.info("AreaManager Path= {}", areaManagerRef.path().toString());
	}
	
	
	private void processShank(int serverId, long serverUid) {
		// akka.tcp://CodeBroker@192.168.0.202:2551/
		if (serverId != ServerEngine.serverId) {
			//TODO
		}
		serverIdMap.put(serverId, getSender().path().parent().toString());
		// akka.tcp://CodeBroker@192.168.0.127:2551/user/WorldActor
		logger.info("get HandShaske {} path {} ", serverId, getSender().path().parent().toString());
	}

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

	private void userConnect2Server(String name, String parms, ActorRef sessionActorRef) {
		// 验证登入
		CodeBrokerAppListener appListener = ContextResolver.getAppListener();
		String handleLogin;
		if (appListener != null) {
			handleLogin = appListener.handleLogin(name, parms);
		} else {
			handleLogin = "Test1234";
		}
		CreateUserWithSession createUserWithSession = new CreateUserWithSession(handleLogin);
		byte[] actorMessageWithSubClass = thriftSerializerFactory
				.getActorMessageWithSubClass(Operation.USER_MANAGER_CREATE_USER_WITH_SESSION, createUserWithSession);
		userManagerRef.tell(actorMessageWithSubClass, sessionActorRef);
	}


	private void processNewCome(long serverUId, String remotePath) {
		// msg.remotePath=akka.tcp://CodeBroker@192.168.0.127:25514
		String fixSupervisorPath = AkkaMediator.getFixSupervisorPath(remotePath, WorldActor.IDENTIFY);
		ActorSelection remoteActorSelection = AkkaMediator.getRemoteActorSelection(fixSupervisorPath);
		
		HandShake handShake=new HandShake(ServerEngine.serverId, serverUId);
		byte[] actorMessageWithSubClass = thriftSerializerFactory.getActorMessageWithSubClass(Operation.WORLD_HAND_SHAKE, handShake);
		
		remoteActorSelection.tell(actorMessageWithSubClass, getSelf());
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
			gmSession.put(COMMAND_ID, actorRef);
		}

		AkkaBootService component = ContextResolver.getComponent(AkkaBootService.class);
		ActorRef localPath = component.getLocalPath(ClusterDistributedPub.IDENTIFY);
		localPath.tell("ssss", ActorRef.noSender());

		actorRef.tell(msg, getSender());
	}



	public static class HelloWorld implements Serializable {

		private static final long serialVersionUID = -3459373230872074079L;

		public final int selfServerId;

		public HelloWorld(int selfServerId) {
			super();
			this.selfServerId = selfServerId;
		}
	}


	public static class RemoveServer {

		public final long serverUId;

		public RemoveServer(long serverUId) {
			super();
			this.serverUId = serverUId;
		}

	}

}
