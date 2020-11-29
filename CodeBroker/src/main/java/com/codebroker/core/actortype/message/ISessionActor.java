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
public interface ISessionActor {

   final class SessionActorAcceptRequest implements ISessionActor {
        public BaseByteArrayPacket request;
        public SessionActorAcceptRequest(BaseByteArrayPacket request) {
            this.request = request;
        }
    }

    final class SessionActorWriteResponse implements ISessionActor {
        public IPacket response;
        @JsonCreator
        public SessionActorWriteResponse(IPacket response) {
            this.response = response;
        }
    }

    final class SessionActorBindingUser implements ISessionActor {
        public  ActorRef<IUserActor> userActorRef;
        @JsonCreator
        public SessionActorBindingUser(ActorRef<IUserActor> userActorRef) {
            this.userActorRef = userActorRef;
        }
    }

	final class SessionActorClose implements ISessionActor {
		public 	boolean enforce;
        @JsonCreator
		public SessionActorClose(boolean enforce) {
			this.enforce = enforce;
		}
    }


    //尝试绑定用户失败
    final class TryBindingUserFail implements ISessionActor {

    }


}
