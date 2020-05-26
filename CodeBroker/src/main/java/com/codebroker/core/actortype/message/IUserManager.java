package com.codebroker.core.actortype.message;

import akka.actor.typed.ActorRef;
import akka.actor.typed.receptionist.Receptionist;
import com.codebroker.protocol.BaseByteArrayPacket;
import com.codebroker.protocol.SerializableType;

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
        public ActorRef<ISession> ioSession;
        public BaseByteArrayPacket message;

        public TryBindingUser(ActorRef<ISession> ioSession, BaseByteArrayPacket message) {
            this.ioSession = ioSession;
            this.message = message;
        }
    }


    final class UserLostSession implements IUserManager {
        public ActorRef<IUser> self;
        public UserLostSession(ActorRef<IUser> self) {
            this.self=self;
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
