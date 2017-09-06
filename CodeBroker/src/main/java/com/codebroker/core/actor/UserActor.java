package com.codebroker.core.actor;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import com.codebroker.api.event.AddEventListener;
import com.codebroker.api.event.HasEventListener;
import com.codebroker.api.event.IEventListener;
import com.codebroker.api.event.RemoveEventListener;
import com.codebroker.core.ContextResolver;
import com.codebroker.core.data.IObject;
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
		    .match(byte[].class, msg -> 
		{
			ActorMessage actorMessage = ThriftSerializerFactory.getActorMessage(msg);
			switch (actorMessage.op) {
			case USER_DISCONNECT:
				//断开链接
				break;
			case USER_SEND_PACKET_TO_IOSESSION:
				sendMessage(actorMessage.messageRaw);
				break;
			case USER_RECIVE_IOSESSION_MESSAGE:
				ReciveIosessionMessage message = new ReciveIosessionMessage();
				ThriftSerializerFactory.deserialize(message, actorMessage.messageRaw);
				handleClientRequest(message.opcode, message.message);
				break;
			case USER_IS_CONNECTED:
				getSender().tell(ioSessionRef == null, getSelf());
				break;
			case USER_REUSER_BINDUSER_IOSESSION_ACTOR:
				// 从新绑定
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

	private void handleClientRequest(int requestId, ByteBuffer params) {
		byte[] msg = new byte[params.remaining()];
		params.get(msg);
		ContextResolver.getAppListener().handleClientRequest(user, requestId, msg);
	}

	private void sendMessage(ByteBuffer byteBuffer) {
		ActorMessage actorMessage = new ActorMessage();
		actorMessage.messageRaw = byteBuffer;
		byte[] bs = ThriftSerializerFactory.getActorMessageWithSubClass(Operation.SESSION_USER_SEND_PACKET,
				actorMessage);
		ioSessionRef.tell(bs, getSelf());
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
	
	

}
