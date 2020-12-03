package com.codebroker.core.actortype.message;

import akka.actor.typed.ActorRef;
import akka.actor.typed.receptionist.Receptionist;
import com.codebroker.api.event.IEvent;
import com.codebroker.api.internal.IPacket;
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


    final class SendEventToGameUser implements IUserManager {

        public final String userId;
        public final IEvent message;
        public final ActorRef<IUserActor> reply;

        public SendEventToGameUser(String userId, IEvent message, ActorRef<IUserActor> reply) {
            this.userId = userId;
            this.message = message;
            this.reply=reply;
        }
    }

    final class SendIPackToGameUser implements IUserManager {

        public final String userId;
        public final IPacket message;
        public final ActorRef<IUserActor> reply;

        public SendIPackToGameUser(String userId, IPacket message, ActorRef<IUserActor> reply) {
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
