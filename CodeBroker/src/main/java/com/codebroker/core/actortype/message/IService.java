package com.codebroker.core.actortype.message;

import akka.actor.typed.ActorRef;
import akka.actor.typed.receptionist.Receptionist;
import akka.cluster.sharding.typed.ShardingEnvelope;
import akka.cluster.sharding.typed.javadsl.EntityTypeKey;
import akka.pattern.StatusReply;
import com.codebroker.core.data.IObject;
import com.codebroker.protocol.SerializableType;

/**
 * 服务规则消息
 */
public interface IService extends SerializableType {

    EntityTypeKey<IService> typeKey =
            EntityTypeKey.create(com.codebroker.core.actortype.message.IService.class,"IService");

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


    interface Reply {
    }

    final class HandleUserMessageBack implements Reply{
        public final IObject object;
        public HandleUserMessageBack(IObject iObject) {
            this.object=iObject;
        }
    }

    final class HandleUserMessage implements IService {

        public final ActorRef<StatusReply<IService.Reply>> Reply;
        public final IObject object;

        public HandleUserMessage(IObject object,ActorRef<StatusReply<IService.Reply>> reply) {
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
