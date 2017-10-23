package com.codebroker.core.cluster;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * 分布式订阅
 *
 * @author xl
 */
public class ClusterDistributedSub extends AbstractActor {

    public static final String IDENTIFY = ClusterDistributedSub.class.getSimpleName();

    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    public ClusterDistributedSub(String topic) {
        ActorRef mediator = DistributedPubSub.get(getContext().system()).mediator();
        mediator.tell(new DistributedPubSubMediator.Subscribe(topic, getSelf()), getSelf());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(Subscribe.class, msg -> {
            ActorRef mediator = DistributedPubSub.get(getContext().system()).mediator();
            mediator.tell(new DistributedPubSubMediator.Subscribe(msg.topic, getSelf()), getSelf());
        }).match(String.class, msg -> {
            log.info("Got: {}", msg);
        }).match(DistributedPubSubMediator.SubscribeAck.class, msg -> log.info("subscribing")).matchAny(msg -> {
            System.out.println(msg);
        }).build();
    }

    public static class Subscribe {
        public final String topic;

        public Subscribe(String topic) {
            super();
            this.topic = topic;
        }
    }
}
