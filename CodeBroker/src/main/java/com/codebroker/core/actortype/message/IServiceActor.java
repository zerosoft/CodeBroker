package com.codebroker.core.actortype.message;

import akka.actor.typed.ActorRef;
import akka.actor.typed.receptionist.Receptionist;
import akka.pattern.StatusReply;

/**
 * 服务规则消息
 */
public interface IServiceActor{

    final class Init implements IServiceActor {
        public Object object;

        public Init(Object obj) {
            this.object=obj;
        }
    }

    final class Destroy implements IServiceActor {
        public Object object;

        public Destroy(Object obj) {
            this.object=obj;
        }
    }


    final class HandleMessage implements IServiceActor {
        public Object object;
        public HandleMessage(Object obj) {
            this.object=obj;
        }
    }


    interface Reply{
    }

    final class HandleUserMessageBack implements IServiceActor.Reply{
        public final Object object;
        public HandleUserMessageBack(Object iObject) {
            this.object=iObject;
        }
    }

    final class HandleUserMessage implements IServiceActor {

        public final ActorRef<StatusReply<IServiceActor.Reply>> Reply;
        public final Object object;

        public HandleUserMessage(Object object,ActorRef<StatusReply<IServiceActor.Reply>> reply) {
            this.Reply = reply;
            this.object = object;
        }
    }

    final class AddProcessorReference implements IServiceActor {

        public Receptionist.Listing listing;

        public AddProcessorReference(Receptionist.Listing listing) {
            this.listing = listing;
        }
    }
}
