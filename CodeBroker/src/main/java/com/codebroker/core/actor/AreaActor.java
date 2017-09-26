package com.codebroker.core.actor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.thrift.TException;

import com.codebroker.api.event.Event;
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
import com.message.thrift.actor.area.CreateGrid;
import com.message.thrift.actor.area.LeaveArea;
import com.message.thrift.actor.area.RemoveGrid;
import com.message.thrift.actor.area.UserEneterArea;
import com.message.thrift.actor.usermanager.CreateUser;

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

	public transient static final String AREA_CLOSE="AREA_CLOSE";
	public transient static final String AREA_ENTER_USER="AREA_ENTER_USER";
	public transient static final String AREA_LEAVE_USER="AREA_LEAVE_USER";
	public transient static final String USER_ID="USER_ID";
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
			 Event event=new Event();
			 event.setTopic(AREA_CLOSE);
			 iUser.tell(event, getSelf());
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
					case AREA_CREATE_GRID:
						CreateGrid createGrid=new CreateGrid();
						thriftSerializerFactory.deserialize(createGrid, actorMessage.messageRaw);
						createGrid(createGrid.getGridId());
						break;
					case AREA_REMOVE_GRID:
						RemoveGrid removeGrid=new RemoveGrid();
						thriftSerializerFactory.deserialize(removeGrid, actorMessage.messageRaw);
						removeGrid(removeGrid.getGridId());
					default:
						break;
					}
				})
		.match(GetGridById.class, msg -> {
			getGridById(msg);
		}).match(GetAllGrids.class, msg -> {
			getAllGrid();
		})
		.match(Terminated.class, msg -> {
			String name = msg.actor().path().name();
		})
		//处理分发事件
		.match(Event.class, msg->{
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
	private void dispatchEvent(Event msg) {
		try {
			IEventListener iEventListener = eventListener.get(msg.getTopic());
			if (iEventListener != null) {
				iEventListener.handleEvent(msg);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	

	private void broadCastAllUser(Event object) {
		Collection<ActorRef> values = userMap.values();
		for (ActorRef iUser : values) {
			iUser.tell(object, getSelf());
		}
	}

	private void createGrid(String gridId) {
		if (gridMap.containsKey(gridId)) {
			getSender().tell(gridMap.get(gridId), getSelf());
		} else {

			Grid gridProxy = new Grid();
			ActorRef actorOf = getContext().actorOf(Props.create(GridActor.class, getSelf()), gridId);
			gridProxy.setActorRef(actorOf);

			getContext().watch(actorOf);
			getSender().tell(gridProxy, getSelf());

			gridMap.put(gridId, gridProxy);

			ServerEngine.envelope.subscribe(actorOf, getSelf().path().name());
		}
	}

	private void removeGrid(String gridId) {
		Grid grid2 = gridMap.get(gridId);
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

			Event event=new Event();
			event.setTopic(AREA_LEAVE_USER);
			IObject iObject=CObject.newInstance();
			iObject.putUtfString(USER_ID, userId);
			event.setMessage(iObject);
			
			// 广播玩家离开
			broadCastAllUser(event);
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
			
			Event event=new Event();
			event.setTopic(AREA_ENTER_USER);
			
			IObject object=CObject.newInstance();
			object.putUtfString(USER_ID, userId);
			event.setMessage(object);
			
			broadCastAllUser(event);
		}
	}


	public static class GetId implements Serializable {

		private static final long serialVersionUID = 3065865091907475834L;
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
