package com.codebroker.core.actor;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import com.codebroker.api.IUser;
import com.codebroker.api.event.IEvent;
import com.codebroker.api.event.IEventListener;
import com.codebroker.api.internal.ByteArrayPacket;
import com.codebroker.core.ContextResolver;
import com.codebroker.core.entities.User;
import com.codebroker.protocol.ThriftSerializerFactory;
import com.message.thrift.actor.ActorMessage;
import com.message.thrift.actor.Operation;
import com.message.thrift.actor.user.ReciveIosessionMessage;

import akka.actor.AbstractActor;
/**
 * 玩家
 * @author xl
 *
 */
import akka.actor.ActorRef;

public class UserActor extends AbstractActor {

	private String userId;
	private String name;

	private final User user;
	private final boolean npc;

	private ActorRef ioSessionRef;

	private ActorRef inGrid;

	private ActorRef inArea;

	private final Map<String, IEventListener> eventListener = new HashMap<String, IEventListener>();

	public UserActor(User user, boolean npc, ActorRef ioSession) {
		super();
		this.user = user;
		this.npc = npc;
		this.ioSessionRef = ioSession;
		user.setActorRef(getSelf());
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder()
		.match(byte[].class, msg -> {
			ActorMessage actorMessage = ThriftSerializerFactory.getActorMessage(msg);
			switch (actorMessage.op) {
			case USER_DISCONNECT:

				break;
			case USER_SEND_PACKET_TO_IOSESSION:
				sendMessage(actorMessage.messageRaw);
				break;	
			case USER_RECIVE_IOSESSION_MESSAGE:
				ReciveIosessionMessage message=new ReciveIosessionMessage();
				ThriftSerializerFactory.deserialize(message, actorMessage.messageRaw);
				handleClientRequest(message.opcode,message.message);
				break;
			case USER_IS_CONNECTED:
				getSender().tell(ioSessionRef == null, getSelf());
				break;
			case USER_REUSER_BINDUSER_IOSESSION_ACTOR:
				//从新绑定
				this.ioSessionRef = getSender();
				break;
				
			case USER_GET_IUSER:
				getSender().tell(user, getSelf());
				break;
				
			case USER_ENTER_AREA:
				if (inArea != null) {
					inArea.tell(new AreaActor.LeaveArea(userId), getSelf());
				}
				inArea = getSender();
				if (inGrid != null) {
					inGrid.tell(new GridActor.LeaveGrid(userId), getSelf());
				}
				inGrid = null;
				break;
			case USER_LEAVE_AREA:
				
				break;
			case USER_ENTER_GRID:
				if (inGrid != null) {
					inGrid.tell(new GridActor.LeaveGrid(userId), getSelf());
				}
				inGrid = getSender();
				break;
			case USER_LEAVE_GRID:
				if (inGrid != null) {
					inGrid.tell(new GridActor.LeaveGrid(userId), getSelf());
				}
				break;
			default:
				
				break;
			}
		})
//		.match(Disconnect.class, msg -> {
//
//		}).match(SendMessage.class, msg -> {
//			sendMessage(msg);
//		})
//		.match(ReciveIosessionMessage.class, msg -> {
//			handleClientRequest(msg);
//		})
//		.match(IsConnected.class, msg -> {
//			getSender().tell(ioSessionRef == null, getSelf());
//		})
//		.match(ReBindIoSession.class, msg -> {
//			this.ioSessionRef = msg.ioSession;
//		})
//		.match(GetIUser.class, msg -> {
//			getSender().tell(user, getSelf());
//		})
				/* 进出区域和格子 */
//				.match(EnterArea.class, msg -> {}).match(LeaveArea.class, msg -> {
//					if (inArea != null) {
//						inArea.tell(new AreaActor.LeaveArea(userId), getSelf());
//					}
//					if (inGrid != null) {
//						inGrid.tell(new GridActor.LeaveGrid(userId), getSelf());
//					}
//					inGrid = null;
//				})
//				.match(EnterGrid.class, msg -> {
//					if (inGrid != null) {
//						inGrid.tell(new GridActor.LeaveGrid(userId), getSelf());
//					}
//					inGrid = getSender();
//				})
//				.match(LeaveGrid.class, msg -> {
//					if (inGrid != null) {
//						inGrid.tell(new GridActor.LeaveGrid(userId), getSelf());
//					}
//				})
				// 事件分发
				.match(AddEventListener.class, msg -> {
					eventListener.put(msg.topic, msg.paramIEventListener);
				}).match(RemoveEventListener.class, msg -> {
					eventListener.remove(msg.topic);
				}).match(HasEventListener.class, msg -> {
					getSender().tell(eventListener.containsKey(msg.topic), getSelf());
				}).match(DispatchEvent.class, msg -> {
					dispatchEvent(msg);
				}).build();
	}

	private void handleClientRequest(int requestId, ByteBuffer params) {
		byte[] msg=new byte[params.remaining()];
		params.get(msg);
		ContextResolver.getAppListener().handleClientRequest(user, requestId, msg);
	}

	private void sendMessage(ByteBuffer byteBuffer) {
		ActorMessage actorMessage=new ActorMessage();
		actorMessage.messageRaw=byteBuffer;
		byte[] bs = ThriftSerializerFactory.getActorMessageWithSubClass(Operation.SESSION_USER_SEND_PACKET, actorMessage);
		ioSessionRef.tell(bs, getSelf());
	}

	/**
	 * 处理分发信息
	 * 
	 * @param msg
	 */
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

}
