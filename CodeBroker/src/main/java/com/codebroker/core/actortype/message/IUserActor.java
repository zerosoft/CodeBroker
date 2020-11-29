package com.codebroker.core.actortype.message;

import akka.actor.typed.ActorRef;
import com.codebroker.api.event.IEvent;
import com.codebroker.api.internal.IPacket;
import com.codebroker.core.data.IObject;
import com.codebroker.protocol.SerializableType;
import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * description
 *
 * @author LongJu
 * @Date 2020/3/25
 */
public interface IUserActor extends SerializableType {

	final class ReceiveMessageFromSession implements IUserActor {
		public IPacket<Integer> message;

		@JsonCreator
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
		public final IObject message;
		public final ActorRef<IUserActor.Reply> replyTo;

		public SendMessageToIServiceActor(String serviceName, IObject message, ActorRef<IUserActor.Reply> replyTo) {
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
		public final IObject message;

		public IObjectReply(IObject message) {
			this.message = message;
		}
	}


	final class SendMessageToGameUserActor implements IUserActor {

		public final String userId;
		public final IObject message;

		public SendMessageToGameUserActor(String userId, IObject message) {
			this.userId = userId;
			this.message = message;
		}
	}

	final class GetSendMessageToGameUserActor implements IUserActor {
		public final IObject message;
//		public final ActorRef<IUser> reply;

		public GetSendMessageToGameUserActor(IObject message) {
			this.message = message;
//			this.reply = reply;
		}
	}
}
