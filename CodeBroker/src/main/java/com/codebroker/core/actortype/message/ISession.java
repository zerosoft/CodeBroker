package com.codebroker.core.actortype.message;


import akka.actor.typed.ActorRef;
import com.codebroker.api.internal.IPacket;
import com.codebroker.protocol.BaseByteArrayPacket;
import com.codebroker.protocol.SerializableType;
import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * description
 *
 * @author LongJu
 * @Date 2020/3/26
 */
public interface ISession {

   final class SessionAcceptRequest implements ISession{
        public BaseByteArrayPacket request;
        public SessionAcceptRequest(BaseByteArrayPacket request) {
            this.request = request;
        }
    }

    final class SessionWriteResponse implements ISession{
        public IPacket response;
        @JsonCreator
        public SessionWriteResponse(IPacket response) {
            this.response = response;
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


    //尝试绑定用户失败
    final class TryBindingUserFail implements ISession{

    }


}
