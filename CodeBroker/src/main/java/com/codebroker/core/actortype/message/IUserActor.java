package com.codebroker.core.actortype.message;

import akka.actor.typed.ActorRef;
import com.codebroker.api.event.Event;
import com.codebroker.api.event.IEvent;
import com.codebroker.api.internal.IPacket;

/**
 * description
 *
 * @author LongJu
 * @Date 2020/3/25
 */
public interface IUserActor{

	final class ReceiveMessageFromSession implements IUserActor {
		public IPacket<Integer> message;

		public ReceiveMessageFromSession(IPacket message) {
			this.message = message;
		}
	}

	final class SendMessageToSession implements IUserActor {
		public IPacket message;

		public SendMessageToSession(IPacket message) {
			this.message = message;
		}
	}

	final class SendMessageToIServiceActor implements IUserActor {

		public final String serviceName;
		public final Object message;
		public final ActorRef<IUserActor.Reply> replyTo;

		public SendMessageToIServiceActor(String serviceName, Object message, ActorRef<IUserActor.Reply> replyTo) {
			this.serviceName = serviceName;
			this.message = message;
			this.replyTo = replyTo;
		}
	}


	enum SessionClose implements IUserActor {
		INSTANCE;
	}

	final class Disconnect implements IUserActor {
		public boolean enforce;

		public Disconnect(boolean enforce) {
			this.enforce = enforce;
		}
	}

	enum NewGameUserActorInit implements IUserActor {
		INSTANCE;
	}

	/**
	 * 新的连接要连接到userActor
	 */
	final class NewSessionLogin implements IUserActor {
		public ActorRef<ISessionActor> iSessionActorRef;

		public NewSessionLogin(ActorRef<ISessionActor> ioSession) {
			this.iSessionActorRef = ioSession;
		}
	}

	final class LogicEvent implements IUserActor {
		public IEvent event;

		public LogicEvent(IEvent event) {
			this.event = event;
		}
	}

	interface Reply {
	}

	final class IObjectReply implements Reply {
		public final Object message;

		public IObjectReply(Object message) {
			this.message = message;
		}
	}


	final class SendIPackToGameUserActor implements IUserActor {

		public final String userId;
		public final IPacket message;

		public SendIPackToGameUserActor(String userId, IPacket message) {
			this.userId = userId;
			this.message = message;
		}
	}

	final class SendEventToGameUserActor implements IUserActor {
		public final IEvent message;

		public SendEventToGameUserActor(IEvent message) {
			this.message = message;
		}
	}
}
