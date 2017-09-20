package com.codebroker.core.actor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.thrift.TException;

import com.codebroker.api.event.EventTypes;
import com.codebroker.api.event.IEventListener;
import com.codebroker.api.event.event.AddEventListener;
import com.codebroker.api.event.event.HasEventListener;
import com.codebroker.api.event.event.RemoveEventListener;
import com.codebroker.core.ServerEngine;
import com.codebroker.core.data.CObject;
import com.codebroker.core.data.IObject;
import com.codebroker.core.entities.Grid;
import com.codebroker.protocol.ThriftSerializerFactory;
import com.message.thrift.actor.ActorMessage;
import com.message.thrift.actor.Operation;
import com.message.thrift.actor.area.LeaveArea;
import com.message.thrift.actor.area.UserEneterArea;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.japi.pf.ReceiveBuilder;

/**
 * 区域 管理区域中的格子
 * 
 * @author zero
 *
 */
public class AreaActor extends AbstractActor {

	private final ActorRef worldRef;
	private final ActorRef userManagerRef;
	ThriftSerializerFactory thriftSerializerFactory=new ThriftSerializerFactory();

	private final Map<String, IEventListener> eventListener = new HashMap<String, IEventListener>();
	// 用户
	private Map<String, ActorRef> userMap = new TreeMap<String, ActorRef>();
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

		 for (ActorRef iUser : userMap.values()) {
			 IObject object=CObject.newInstance();
			 object.putInt(EventTypes.KEY, EventTypes.AREA_BROAD_CAST);
			 iUser.tell(object, getSelf());
		 }
	}

	@Override
	public Receive createReceive() {
		return ReceiveBuilder.create()
				.match(byte[].class, msg->{
					 ActorMessage actorMessage = thriftSerializerFactory.getActorMessage(msg);
					 switch (actorMessage.op) {
					case AREA_USER_ENTER_AREA:
						UserEneterArea eneterArea=new UserEneterArea();
						thriftSerializerFactory.deserialize(eneterArea, actorMessage.messageRaw);
						enterArea(eneterArea.userId,getSender());
						break;
					case AREA_USER_LEAVE_AREA:
						LeaveArea leaveArea=new LeaveArea();
						thriftSerializerFactory.deserialize(leaveArea, actorMessage.messageRaw);
						leaveArea(leaveArea.userId);
						break;
					default:
						break;
					}
				})
		.match(CreateGrid.class, msg -> {
			createGrid(msg);
		}).match(RemoveGrid.class, msg -> {
			removeGrid(msg);
		}).match(GetGridById.class, msg -> {
			getGridById(msg);
		}).match(GetAllGrids.class, msg -> {
			getAllGrid();
		})
		.match(Terminated.class, msg -> {
			String name = msg.actor().path().name();
		})
		// 广播信息
		.match(IObject.class, msg -> {
			broadCastAllUser(msg);
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
	

	private void broadCastAllUser(IObject object) {
		Collection<ActorRef> values = userMap.values();
		object.putInt(EventTypes.KEY, EventTypes.AREA_BROAD_CAST);
		for (ActorRef iUser : values) {
			iUser.tell(object, getSelf());
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

	private void leaveArea(String userId) {
		if (userMap.containsKey(userId)) {
			userMap.remove(userId);

			IObject iObject=CObject.newInstance();
			iObject.putUtfString("userid", userId);
			// 广播玩家离开
			broadCastAllUser(iObject);
		}
	}


	private void enterArea(String userId, ActorRef sender) {
		if (userMap.containsKey(userId)) {
			getSender().tell(false, getSelf());
		} else {
			userMap.put(userId, sender);
			getSender().tell(true, getSelf());
			// 通知user进入所在actor

			byte[] tbaseMessage;
			try {
				tbaseMessage = thriftSerializerFactory.getTbaseMessage(Operation.USER_ENTER_AREA);
				sender.tell(tbaseMessage, getSelf());

			} catch (TException e) {
				e.printStackTrace();
			}
			IObject object=CObject.newInstance();
			object.putInt(EventTypes.KEY, EventTypes.AREA_BROAD_CAST);
			object.putUtfString(EventTypes.AREA_USER_ENTER_AREA, userId);
			broadCastAllUser(object);
		}
	}


	public static class GetId implements Serializable {

		private static final long serialVersionUID = 3065865091907475834L;
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



	public static class GetPlayers implements Serializable {

		private static final long serialVersionUID = -1823532488763688181L;

	}
}
