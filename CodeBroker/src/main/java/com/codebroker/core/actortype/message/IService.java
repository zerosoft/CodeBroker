package com.codebroker.core.actortype.message;

import akka.actor.typed.ActorRef;
import akka.actor.typed.receptionist.Receptionist;
import com.codebroker.core.data.IObject;

/**
 * 服务规则消息
 */
public interface IService{

    final class Init implements IService {
        public Object object;

        public Init(Object obj) {
            this.object=obj;
        }
    }

    final class Destroy implements IService {
        public Object object;

        public Destroy(Object obj) {
            this.object=obj;
        }
    }


    final class HandleMessage implements IService {
        public IObject object;
        public HandleMessage(IObject obj) {
            this.object=obj;
        }
    }

    final class HandleUserMessage implements IService {
        public final ActorRef<IUser> Reply;
        public final IObject object;

        public HandleUserMessage(ActorRef<IUser> reply, IObject object) {
            Reply = reply;
            this.object = object;
        }
    }

    final class AddProcessorReference implements IService {

        public Receptionist.Listing listing;

        public AddProcessorReference(Receptionist.Listing listing) {
            this.listing = listing;
        }
    }
}
