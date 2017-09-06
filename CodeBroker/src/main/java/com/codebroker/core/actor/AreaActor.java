package com.codebroker.core.actor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.apache.thrift.TException;

import com.codebroker.api.IUser;
import com.codebroker.api.event.AddEventListener;
import com.codebroker.api.event.HasEventListener;
import com.codebroker.api.event.IEventListener;
import com.codebroker.api.event.RemoveEventListener;
import com.codebroker.core.ServerEngine;
import com.codebroker.core.data.IObject;
import com.codebroker.core.entities.Grid;
import com.codebroker.core.entities.User;
import com.codebroker.protocol.ThriftSerializerFactory;
import com.message.thrift.actor.ActorMessage;
import com.message.thrift.actor.Operation;
import com.message.thrift.actor.area.UserEneterArea;
import com.message.thrift.actor.usermanager.CreateUser;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
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

	private final ActorRef worldRef;
	private final ActorRef userManagerRef;

	private final Map<String, IEventListener> eventListener = new HashMap<String, IEventListener>();
	// 用户
	private Map<String, ActorRef> userMap = new TreeMap<String, ActorRef>();
	// 创建NPC
	private Map<String, ActorRef> npcMap = new TreeMap<String, ActorRef>();
	// 格子
	private Map<String, Grid> gridMap = new TreeMap<String, Grid>();

	public AreaActor(ActorRef worldRef, ActorRef userManagerRef) {
		super();
		this.worldRef = worldRef;
		this.userManagerRef = userManagerRef;
	}

	@Override
	public void postStop() throws Exception {
		super.postStop();
		Iterable<ActorRef> children = getContext().getChildren();
		for (ActorRef childRef : children) {
			childRef.tell(PoisonPill.getInstance(), getSelf());
		}

		// for (ActorRef iUser : userMap.values()) {
		// iUser.dispatchEvent(new CodeEvent());
		// }
		//
		// for (ActorRef user : npcMap.values()) {
		// try {
		// user.getActorRef().tell(PoisonPill.getInstance(), getSelf());
		// } catch (NoActorRefException e) {
		//
		// }
		// }
	}

	@Override
	public Receive createReceive() {
		return ReceiveBuilder.create()
				.match(byte[].class, msg->{
					 ActorMessage actorMessage = ThriftSerializerFactory.getActorMessage(msg);
					 switch (actorMessage.op) {
					case AREA_CREATE_NPC:
						createNPC();
						break;
					case AREA_USER_ENTER_AREA:
						UserEneterArea eneterArea=new UserEneterArea();
						ThriftSerializerFactory.deserialize(eneterArea, actorMessage.messageRaw);
						enterArea(eneterArea.userId,getSender());
						break;
					default:
						break;
					}
				})
		.match(ActorMessage.class, msg -> {
			switch (msg.op) {
			case AREA_CREATE_NPC:
				createNPC();
				break;
			case AREA_USER_ENTER_AREA:
				
				break;
			case AREA_USER_LEAVE_AREA:
				break;
			default:
				break;
			}
		}).match(EnterArea.class, msg -> {
//			(msg);
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
		})
		.match(GetPlayers.class, msg -> {
//			Collection<IUser> values = userMap.values();
//			List<IUser> list = new ArrayList<IUser>();
//			list.addAll(values);
//			getSender().tell(list, getSelf());
		})
		.match(Terminated.class, msg -> {
			String name = msg.actor().path().name();
		})
		// 广播信息
		.match(BroadCastAllUser.class, msg -> {
			broadCastAllUser(msg.jsonString);
		})
		//处理分发事件
		.match(IObject.class, msg->{
			dispatchEvent(msg);		
		})
		//绑定本地事件处理
		.match(AddEventListener.class, msg -> {
			eventListener.put(msg.topic, msg.paramIEventListener);
		}).match(RemoveEventListener.class, msg -> {
			eventListener.remove(msg.topic);
		}).match(HasEventListener.class, msg -> {
			getSender().tell(eventListener.containsKey(msg.topic), getSelf());
		})
		.build();
	}

	/**
	 * 处理分发信息
	 * 
	 * @param msg
	 */
	private void dispatchEvent(IObject msg) {
		String topic = msg.getUtfString("e");
		try {
			IEventListener iEventListener = eventListener.get(topic);
			if (iEventListener != null) {
				iEventListener.handleEvent(topic,msg);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	

	private void broadCastAllUser(String jsonString) {
//		Collection<IUser> values = userMap.values();
//		CodeEvent codeEvent = new CodeEvent();
//		codeEvent.setParameter(jsonString);
//		codeEvent.setTopic(CodeBrokerEvent.AREA_EVENT);
//		for (IUser iUser : values) {
//			iUser.dispatchEvent(codeEvent);
//		}
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

//	private void enterArea(EnterArea msg) throws Exception {
//		if (userMap.containsKey(msg.user.getUserId())) {
//			getSender().tell(false, getSelf());
//		} else {
//			userMap.put(msg.user.getUserId(), msg.user);
//			getSender().tell(true, getSelf());
//			// 通知user进入所在actor
//
//			byte[] tbaseMessage = ThriftSerializerFactory.getTbaseMessage(Operation.USER_ENTER_AREA);
//			// new UserActor.EnterArea()
//			((User) msg.user).getActorRef().tell(tbaseMessage, getSelf());
//
//			broadCastAllUser("JSON　SOMEONE ENTER");
//		}
//	}

	private void enterArea(String userId, ActorRef sender) {
		if (userMap.containsKey(userId)) {
			getSender().tell(false, getSelf());
		} else {
			userMap.put(userId, sender);
			getSender().tell(true, getSelf());
			// 通知user进入所在actor

			byte[] tbaseMessage;
			try {
				tbaseMessage = ThriftSerializerFactory.getTbaseMessage(Operation.USER_ENTER_AREA);
				sender.tell(tbaseMessage, getSelf());

			} catch (TException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// new UserActor.EnterArea()
		
			broadCastAllUser("JSON　SOMEONE ENTER");
		}
	}

	private void createNPC() throws Exception {
		Timeout timeout = new Timeout(Duration.create(5, "seconds"));

		CreateUser createUser = new CreateUser(true, UUID.randomUUID().toString());
		byte[] actorMessageWithSubClass = ThriftSerializerFactory.getActorMessageWithSubClass(Operation.USER_MANAGER_CREATE_USER, createUser);
		Future<Object> future = Patterns.ask(userManagerRef, actorMessageWithSubClass, timeout);

		User result = (User) Await.result(future, timeout.duration());
//		npcMap.put(result.getUserId(), result);

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





	public static class GetPlayers implements Serializable {

		private static final long serialVersionUID = -1823532488763688181L;

	}
}
