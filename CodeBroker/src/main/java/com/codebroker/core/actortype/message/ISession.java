package com.codebroker.core.actortype.message;


import akka.actor.typed.ActorRef;
import com.codebroker.api.internal.IPacket;
import com.codebroker.protocol.BaseByteArrayPacket;
import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * description
 *
 * @author LongJu
 * @Date 2020/3/26
 */
public interface ISession {

   final class SessionAcceptMessage implements ISession{
        public BaseByteArrayPacket message;
        public SessionAcceptMessage(BaseByteArrayPacket message) {
            this.message = message;
        }
    }

    final class SessionSendMessage implements ISession{
        public IPacket message;
        @JsonCreator
        public SessionSendMessage(IPacket message) {
            this.message = message;
        }
    }

    final class SessionBindingUser implements ISession{
        public  ActorRef<IUser> userActorRef;
        @JsonCreator
        public SessionBindingUser(ActorRef<IUser> userActorRef) {
            this.userActorRef = userActorRef;
        }
    }

	final class  SessionClose implements ISession{
		public 	boolean enforce;
        @JsonCreator
		public SessionClose(boolean enforce) {
			this.enforce = enforce;
		}
    }


}
