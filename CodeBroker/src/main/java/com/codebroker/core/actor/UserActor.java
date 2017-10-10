package com.codebroker.core.actor;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.apache.thrift.TException;

import com.codebroker.api.NPCControl;
import com.codebroker.api.event.Event;
import com.codebroker.api.event.IEventListener;
import com.codebroker.api.event.event.AddEventListener;
import com.codebroker.api.event.event.HasEventListener;
import com.codebroker.api.event.event.RemoveEventListener;
import com.codebroker.core.ContextResolver;
import com.codebroker.core.data.IObject;
import com.codebroker.core.entities.User;
import com.codebroker.protocol.ThriftSerializerFactory;
import com.message.thrift.actor.ActorMessage;
import com.message.thrift.actor.Operation;
import com.message.thrift.actor.area.LeaveArea;
import com.message.thrift.actor.user.ReciveIosessionMessage;
import com.message.thrift.actor.usermanager.RemoveUser;

import akka.actor.AbstractActor;
/**
 * 玩家
 * @author xl
 *
 */
import akka.actor.ActorRef;

public class UserActor extends AbstractActor {
	ThriftSerializerFactory thriftSerializerFactory = new ThriftSerializerFactory();
	private String userId;
	private final User user;

	private ActorRef ioSessionRef;
	private NPCControl npcControl;

	private ActorRef userManagerRef;

	private ActorRef inGrid;
	private ActorRef inArea;

	private final Map<String, IEventListener> eventListener = new HashMap<String, IEventListener>();

	/**
	 * 创建NPC
	 * 
	 * @param npcId
	 * @param user
	 * @param npcControl
	 * @param userManagerRef
	 */
	public UserActor(String npcId, User user, NPCControl npcControl, ActorRef userManagerRef) {
		super();
		this.userId = npcId;
		this.user = user;
		this.npcControl = npcControl;
		this.userManagerRef = userManagerRef;
		user.setActorRef(getSelf());
	}

	/**
	 * 创建用户
	 * 
	 * @param user
	 * @param ioSession
	 * @param userManagerRef
	 */
	public UserActor(User user, ActorRef ioSession, ActorRef userManagerRef) {
		super();
		this.user = user;
		this.userId = getSelf().path().name();
		this.ioSessionRef = ioSession;
		this.userManagerRef = userManagerRef;
		user.setActorRef(getSelf());
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder().match(byte[].class, msg -> {
			ActorMessage actorMessage = thriftSerializerFactory.getActorMessage(msg);
			switch (actorMessage.op) {
			case USER_DISCONNECT:
				// 切断网络
				byte[] tbaseMessage = thriftSerializerFactory.getTbaseMessage(Operation.SESSION_USER_LOGOUT);
				ioSessionRef.tell(tbaseMessage, getSelf());
				// 管理器移除
				RemoveUser removeUser = new RemoveUser(userId);
				byte[] actorMessageWithSubClass = thriftSerializerFactory
						.getActorMessageWithSubClass(Operation.USER_MANAGER_REMOVE_USER, removeUser);
				userManagerRef.tell(actorMessageWithSubClass, getSelf());

				if (inGrid != null) {

				}

				if (inArea != null) {

				}
				
				ContextResolver.getAppListener().handleLogout(user);
				// 断开链接
				break;
			case USER_SEND_PACKET_TO_IOSESSION:
				sendMessage(actorMessage.messageRaw);
				break;
			case USER_RECIVE_IOSESSION_MESSAGE:
				ReciveIosessionMessage message = new ReciveIosessionMessage();
				thriftSerializerFactory.deserialize(message, actorMessage.messageRaw);
				handleClientRequest(message.opcode, message.message);
				break;
			case USER_IS_CONNECTED:
				getSender().tell(ioSessionRef == null, getSelf());
				break;
			case USER_RE_BINDUSER_IOSESSION_ACTOR:
				// 从新绑定
				this.ioSessionRef = getSender();
				break;

			case USER_GET_IUSER:
				getSender().tell(user, getSelf());
				break;

			case USER_ENTER_AREA:
				if (inArea != null) {
					LeaveArea leaveArea = new LeaveArea(userId);
					inArea.tell(thriftSerializerFactory.getActorMessageWithSubClass(Operation.AREA_USER_LEAVE_AREA,
							leaveArea), getSelf());
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
			// 处理分发事件
			.match(Event.class, msg -> {
				if (npcControl != null) {
					npcControl.execute(msg);
				} else {
					dispatchEvent(msg);
				}
			}).match(IObject.class, msg -> {
			})
			// 绑定本地事件处理
			.match(AddEventListener.class, msg -> {
				eventListener.put(msg.topic, msg.paramIEventListener);
			}).match(RemoveEventListener.class, msg -> {
				eventListener.remove(msg.topic);
			}).match(HasEventListener.class, msg -> {
				getSender().tell(eventListener.containsKey(msg.topic), getSelf());
			}).build();
	}

	private void handleClientRequest(int requestId, ByteBuffer params) {
		byte[] msg = new byte[params.remaining()];
		params.get(msg);
		ContextResolver.getAppListener().handleClientRequest(user, requestId, msg);
	}

	private void sendMessage(ByteBuffer byteBuffer) {
		ActorMessage actorMessage = new ActorMessage();
		actorMessage.messageRaw = byteBuffer;
		actorMessage.op=Operation.SESSION_USER_SEND_PACKET;
		try {
			byte[] bs = thriftSerializerFactory.getActorMessage(actorMessage);
			ioSessionRef.tell(bs, getSelf());
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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

}
