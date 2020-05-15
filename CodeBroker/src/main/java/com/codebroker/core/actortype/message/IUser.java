package com.codebroker.core.actortype.message;

import akka.actor.typed.ActorRef;
import com.codebroker.api.event.Event;
import com.codebroker.api.event.IEvent;
import com.codebroker.api.internal.IPacket;
import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * description
 *
 * @author LongJu
 * @Date 2020/3/25
 */
public interface IUser {

	final class ReceiveMessageFromSession implements IUser{
		public IPacket message;
		@JsonCreator
		public ReceiveMessageFromSession(IPacket message) {
			this.message = message;
		}
	}

	final class SendMessageToSession implements IUser{
		public IPacket message;
		public SendMessageToSession(IPacket message) {
			this.message = message;
		}
	}

	enum SessionClose implements IUser{
		INSTANCE;
	}

	final class Disconnect implements IUser{
		public 	boolean enforce;

		public Disconnect(boolean enforce) {
			this.enforce = enforce;
		}
	}

	enum  NewGameUserInit implements IUser{
		INSTANCE;
	}
	/**
	 * 新的连接要连接到userActor
	 */
	final class NewSessionLogin implements IUser {
		public ActorRef<ISession> iSessionActorRef;
		public NewSessionLogin(ActorRef<ISession> ioSession) {
			this.iSessionActorRef=ioSession;
		}
	}

	final class LogicEvent implements IUser {
		public IEvent event;
		public LogicEvent(IEvent event) {
			this.event=event;
		}
	}
}
