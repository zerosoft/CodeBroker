package com.codebroker.core.actor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.codebroker.api.event.IEvent;
import com.codebroker.api.event.IEventListener;
import com.codebroker.api.internal.IoMessagePackage;
import com.codebroker.core.ContextResolver;
import com.codebroker.core.entities.User;

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
		  .match(Disconnect.class, msg -> {

		}).match(SendMessage.class, msg -> {
			sendMessage(msg);
		}).match(ReciveIosessionMessage.class, msg -> {
			handleClientRequest(msg);
		}).match(IsConnected.class, msg -> {
			getSender().tell(ioSessionRef == null, getSelf());
		}).match(ReBindIoSession.class, msg -> {
			this.ioSessionRef = msg.ioSession;
		}).match(GetIUser.class, msg -> {
			getSender().tell(user, getSelf());
		})
		/* 进出区域和格子 */
		.match(EnterArea.class, msg -> {
			if (inArea != null) {
				inArea.tell(new AreaActor.LeaveArea(userId), getSelf());
			}
			inArea = getSender();
			if (inGrid != null) {
				inGrid.tell(new GridActor.LeaveGrid(userId), getSelf());
			}
			inGrid=null;
		}).match(LeaveArea.class, msg -> {
			if (inArea != null) {
				inArea.tell(new AreaActor.LeaveArea(userId), getSelf());
			}
			if (inGrid != null) {
				inGrid.tell(new GridActor.LeaveGrid(userId), getSelf());
			}
			inGrid=null;
		}).match(EnterGrid.class, msg -> {
			if (inGrid != null) {
				inGrid.tell(new GridActor.LeaveGrid(userId), getSelf());
			}
			inGrid = getSender();
		}).match(LeaveGrid.class, msg -> {
			if (inGrid != null) {
				inGrid.tell(new GridActor.LeaveGrid(userId), getSelf());
			}
		})
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

	private void handleClientRequest(ReciveIosessionMessage msg) {
		ContextResolver.getAppListener().handleClientRequest(user, msg.message.getOpCode(), msg.message.getRawData());
	}

	private void sendMessage(SendMessage msg) {
		SessionActor.UserSendMessage2Net msg2 = new SessionActor.UserSendMessage2Net(msg.requestId,
				(byte[]) msg.message);
		ioSessionRef.tell(msg2, getSelf());
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

	public static class IsConnected implements Serializable {

		private static final long serialVersionUID = 2609073574533558230L;
	}

	public static class Disconnect implements Serializable {

		private static final long serialVersionUID = -2904174871036885347L;
	}

	public static class ReciveIosessionMessage implements Serializable {

		private static final long serialVersionUID = 3590058049590298359L;
		public final IoMessagePackage message;

		public ReciveIosessionMessage(IoMessagePackage message) {
			super();
			this.message = message;
		}
	}

	public static class ReBindIoSession implements Serializable {
		private static final long serialVersionUID = 5344680707079139938L;
		public final ActorRef ioSession;

		public ReBindIoSession(ActorRef ioSession) {
			super();
			this.ioSession = ioSession;
		}

	}

	public static class GetIUser implements Serializable {
		private static final long serialVersionUID = 1564009987311559165L;
	}

	public static class SendMessage implements Serializable {
		private static final long serialVersionUID = -3956993228915965382L;
		public final int requestId;
		public final Object message;

		public SendMessage(int requestId, Object message) {
			super();
			this.requestId = requestId;
			this.message = message;
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

	public static class EnterArea implements Serializable {
		private static final long serialVersionUID = 392796592618360211L;
	}

	public static class LeaveArea implements Serializable {
		private static final long serialVersionUID = -1064639229891100392L;
	}

	public static class EnterGrid implements Serializable {
		private static final long serialVersionUID = -3324165519207134835L;
	}

	public static class LeaveGrid implements Serializable {
		private static final long serialVersionUID = -4525911314879048878L;
	}
}
