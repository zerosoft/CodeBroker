package com.codebroker.core.actortype.message;

import akka.actor.typed.ActorRef;
import akka.actor.typed.receptionist.Receptionist;
import com.codebroker.core.data.IObject;
import com.codebroker.protocol.BaseByteArrayPacket;

/**
 * description
 *
 * @author LongJu
 * @Date 2020/3/25
 */
public interface IUserManager  {
    /**
     * 添加处理器参考
     */
    final class AddProcessorReference implements IUserManager{

        public Receptionist.Listing listing;

        public AddProcessorReference(Receptionist.Listing listing) {
            this.listing = listing;
        }
    }

    //尝试绑定用户
    final class TryBindingUser implements IUserManager{
        public ActorRef<ISessionActor> ioSession;
        public BaseByteArrayPacket message;

        public TryBindingUser(ActorRef<ISessionActor> ioSession, BaseByteArrayPacket message) {
            this.ioSession = ioSession;
            this.message = message;
        }
    }



    final class UserLostSession implements IUserManager {
        public ActorRef<IUserActor> self;
        public UserLostSession(ActorRef<IUserActor> self) {
            this.self=self;
        }
    }


        final class SendMessageToGameUser implements IUserManager {

            public final String userId;
            public final IObject message;
            public final ActorRef<IUserActor> reply;

            public SendMessageToGameUser(String userId, IObject message, ActorRef<IUserActor> reply) {
                this.userId = userId;
                this.message = message;
                this.reply=reply;
            }
        }

    final class UserClose implements IUserManager {
        public String uid;
        public UserClose(String uid) {
            this.uid=uid;
        }
    }

    enum  TimeCheck implements IUserManager{
        INSTANCE;
    }


}
