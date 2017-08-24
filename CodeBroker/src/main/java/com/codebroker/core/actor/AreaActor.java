package com.codebroker.core.actor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import com.codebroker.api.IUser;
import com.codebroker.api.event.IEvent;
import com.codebroker.api.event.IEventListener;
import com.codebroker.core.ServerEngine;
import com.codebroker.core.entities.Area;
import com.codebroker.core.entities.CodeBrokerEvent;
import com.codebroker.core.entities.CodeEvent;
import com.codebroker.core.entities.Grid;
import com.codebroker.core.entities.User;
import com.codebroker.core.eventbus.CodebrokerEnvelope;
import com.codebroker.exception.NoActorRefException;
import com.message.thrift.actor.ActorMessage;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.japi.pf.ReceiveBuilder;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

/**
 * 区域 管理区域中的格子
 * 
 * @author zero
 *
 */
public class AreaActor extends AbstractActor {

	private final ActorRef world;

	private final Map<String, IEventListener> eventListener = new HashMap<String, IEventListener>();
	// 用户
	private Map<String, IUser> userMap = new TreeMap<String, IUser>();
	// 创建NPC
	private Map<String, User> npcMap = new TreeMap<String, User>();
	// 格子
	private Map<String, Grid> gridMap = new TreeMap<String, Grid>();

	public AreaActor(ActorRef world) {
		super();
		this.world = world;

	}

	@Override
	public void postStop() throws Exception {
		super.postStop();
		Iterable<ActorRef> children = getContext().getChildren();
		for (ActorRef childRef : children) {
			childRef.tell(PoisonPill.getInstance(), getSelf());
		}

		for (IUser iUser : userMap.values()) {
			iUser.dispatchEvent(new CodeEvent());
		}

		for (User user : npcMap.values()) {
			try {
				user.getActorRef().tell(PoisonPill.getInstance(), getSelf());
			} catch (NoActorRefException e) {

			}
		}
	}

	@Override
	public Receive createReceive() {
		return ReceiveBuilder.create()
		.match(ActorMessage.class, msg -> {
			switch (msg.op) {
			case AREA_CREATE_NPC:
				createNPC();
				break;

			default:
				break;
			}
		}).match(EnterArea.class, msg -> {
			enterArea(msg);
		}).match(LeaveArea.class, msg -> {
			leaveArea(msg);
		}).match(CreateGrid.class, msg -> {
			createGrid(msg);
		}).match(RemoveGrid.class, msg -> {
			removeGrid(msg);
		}).match(GetGridById.class, msg -> {
			getGridById(msg);
		}).match(GetAllGrids.class, msg -> {
			getAllGrid();
		}).match(GetPlayers.class, msg -> {
			Collection<IUser> values = userMap.values();
			List<IUser> list = new ArrayList<IUser>();
			list.addAll(values);
			getSender().tell(list, getSelf());
		}).match(Terminated.class, msg -> {
			String name = msg.actor().path().name();
		})
				// 广播信息
				.match(BroadCastAllUser.class, msg -> {
					broadCastAllUser(msg.jsonString);
				}).match(AddEventListener.class, msg -> {
					eventListener.put(msg.topic, msg.paramIEventListener);
				}).match(RemoveEventListener.class, msg -> {
					eventListener.remove(msg.topic);
				}).match(HasEventListener.class, msg -> {
					getSender().tell(eventListener.containsKey(msg.topic), getSelf());
				}).match(DispatchEvent.class, msg -> {
					dispatchEvent(msg);
				}).build();
	}

	private void dispatchEvent(DispatchEvent msg) {
		IEvent paramIEvent = msg.paramIEvent;
		try {
			IEventListener iEventListener = eventListener.get(paramIEvent.getTopic());
			if (iEventListener != null) {
				iEventListener.handleEvent(paramIEvent);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void broadCastAllUser(String jsonString) {
		Collection<IUser> values = userMap.values();
		CodeEvent codeEvent = new CodeEvent();
		codeEvent.setParameter(jsonString);
		codeEvent.setTopic(CodeBrokerEvent.AREA_EVENT);
		for (IUser iUser : values) {
			iUser.dispatchEvent(codeEvent);
		}
	}

	private void createGrid(CreateGrid msg) {
		if (gridMap.containsKey(msg.gridId)) {
			getSender().tell(gridMap.get(msg.gridId), getSelf());
		} else {

			Grid gridProxy = new Grid();
			ActorRef actorOf = getContext().actorOf(Props.create(GridActor.class, getSelf()), msg.gridId);
			gridProxy.setActorRef(actorOf);

			getContext().watch(actorOf);
			getSender().tell(gridProxy, getSelf());

			gridMap.put(msg.gridId, gridProxy);

			ServerEngine.envelope.subscribe(actorOf, getSelf().path().name());
		}
	}

	private void removeGrid(RemoveGrid msg) {
		Grid grid2 = gridMap.get(msg.gridId);
		if (grid2 != null) {
			ServerEngine.envelope.unsubscribe(grid2.getActorRef());
			grid2.destory();
		}
	}

	private void getAllGrid() {
		Collection<Grid> values = gridMap.values();
		List<Grid> list = new ArrayList<Grid>();
		list.addAll(values);
		getSender().tell(list, getSelf());
	}

	private void getGridById(GetGridById msg) {
		Grid grid2 = gridMap.get(msg.gridId);
		if (grid2 != null) {
			getSender().tell(grid2, getSelf());
		}
	}

	private void leaveArea(LeaveArea msg) {
		if (userMap.containsKey(msg.userId)) {
			userMap.remove(msg.userId);

			// 广播玩家离开
			broadCastAllUser("JSON　SOMEONE LEAVE");
		}
	}

	private void enterArea(EnterArea msg) throws Exception {
		if (userMap.containsKey(msg.user.getUserId())) {
			getSender().tell(false, getSelf());
		} else {
			userMap.put(msg.user.getUserId(), msg.user);
			getSender().tell(true, getSelf());
			// 通知user进入所在actor
			((User) msg.user).getActorRef().tell(new UserActor.EnterArea(), getSelf());

			broadCastAllUser("JSON　SOMEONE ENTER");
		}
	}

	private void createNPC() throws Exception {
		ActorSelection actorSelection = getContext()
				.actorSelection("/user/" + WorldActor.IDENTIFY + "/" + UserManagerActor.IDENTIFY);
		Timeout timeout = new Timeout(Duration.create(5, "seconds"));
		Future<Object> future = Patterns.ask(actorSelection,
				new UserManagerActor.CreateUser(true, UUID.randomUUID().toString()), timeout);
		User result = (User) Await.result(future, timeout.duration());
		npcMap.put(result.getUserId(), result);

		getSender().tell(result, getSelf());
	}

	public static class GetId implements Serializable {

		private static final long serialVersionUID = 3065865091907475834L;
	}

	public static class EnterArea implements Serializable {

		private static final long serialVersionUID = -7843526305386726113L;

		public final IUser user;

		public EnterArea(IUser user) {
			super();
			this.user = user;
		}

	}

	public static class LeaveArea implements Serializable {
		private static final long serialVersionUID = 1388965307152127227L;
		public final String userId;

		public LeaveArea(String userId) {
			super();
			this.userId = userId;
		}

	}

	public static class CreateGrid implements Serializable {
		private static final long serialVersionUID = 5561938563026536958L;
		public final String gridId;

		public CreateGrid(String gridId) {
			super();
			this.gridId = gridId;
		}

	}

	public static class RemoveGrid implements Serializable {
		private static final long serialVersionUID = -3522073369320700653L;
		public final String gridId;

		public RemoveGrid(String gridId) {
			super();
			this.gridId = gridId;
		}
	}

	public static class GetGridById implements Serializable {
		private static final long serialVersionUID = -4927817351189923926L;
		public final String gridId;

		public GetGridById(String gridId) {
			super();
			this.gridId = gridId;
		}

	}

	public static class GetAllGrids implements Serializable {

		private static final long serialVersionUID = -1778022483881100165L;

	}

	public static class BroadCastAllUser implements Serializable {

		private static final long serialVersionUID = -2029234957042497669L;

		public final String jsonString;

		public BroadCastAllUser(String jsonString) {
			super();
			this.jsonString = jsonString;
		}
	}

	public static class AddEventListener {
		public final String topic;
		public final IEventListener paramIEventListener;

		public AddEventListener(String topic, IEventListener paramIEventListener) {
			super();
			this.topic = topic;
			this.paramIEventListener = paramIEventListener;
		}

	}

	public static class RemoveEventListener {
		public final String topic;

		public RemoveEventListener(String topic) {
			super();
			this.topic = topic;
		}
	}

	public static class DispatchEvent implements Serializable {

		private static final long serialVersionUID = -382183759904733665L;

		public final IEvent paramIEvent;

		public DispatchEvent(IEvent paramIEvent) {
			super();
			this.paramIEvent = paramIEvent;
		}

	}

	public static class HasEventListener implements Serializable {

		private static final long serialVersionUID = 6661678840156738466L;

		public final String topic;

		public HasEventListener(String topic) {
			super();
			this.topic = topic;
		}

	}

	public static class GetPlayers implements Serializable {

		private static final long serialVersionUID = -1823532488763688181L;

	}
}
