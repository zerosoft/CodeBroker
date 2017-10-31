package com.codebroker.core.cluster;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;
import com.codebroker.core.ContextResolver;
import com.codebroker.core.manager.CacheManager;

/**
 * 分布式发布信息
 *
 * @author xl
 */
public class ClusterDistributedPub extends AbstractActor {

    public static final String IDENTIFY = ClusterDistributedPub.class.getSimpleName();
    // activate the extension
    ActorRef mediator = DistributedPubSub.get(getContext().system()).mediator();

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(String.class, in -> {
            String out = in.toUpperCase();
            mediator.tell(new DistributedPubSubMediator.Publish("content", out), getSelf());

        }).
                // 发布到本地的
                        match(PublicLocalAffinityChannel.class, msg -> {
                    CacheManager component = ContextResolver.getComponent(CacheManager.class);
                    ActorRef localPath = component.getLocalPath(ClusterDistributedSub.IDENTIFY);
                    DistributedPubSubMediator.Send message = new DistributedPubSubMediator.Send(
                            localPath.path().toString(), msg, msg.local);
                    mediator.tell(message, getSelf());
                }).match(PublicChannel.class, msg -> {
                    mediator.tell(new DistributedPubSubMediator.Publish(msg.channelName, msg.message), getSelf());
                }).build();
    }

    public static class PublicLocalAffinityChannel {
        private String channelName;
        private Object message;
        private boolean local;

        public PublicLocalAffinityChannel(String channelName, Object message) {
            super();
            this.channelName = channelName;
            this.message = message;
            this.local = true;
        }

        public PublicLocalAffinityChannel(String channelName, Object message, boolean local) {
            super();
            this.channelName = channelName;
            this.message = message;
            this.local = local;
        }

        public String getChannelName() {
            return channelName;
        }

        public void setChannelName(String channelName) {
            this.channelName = channelName;
        }

        public Object getMessage() {
            return message;
        }

        public void setMessage(Object message) {
            this.message = message;
        }

        public boolean isLocal() {
            return local;
        }

        public void setLocal(boolean local) {
            this.local = local;
        }

    }

    public static class PublicChannel {
        private String channelName;
        private Object message;

        public PublicChannel(String channelName, Object message) {
            super();
            this.channelName = channelName;
            this.message = message;
        }

        public String getChannelName() {
            return channelName;
        }

        public void setChannelName(String channelName) {
            this.channelName = channelName;
        }

        public Object getMessage() {
            return message;
        }

        public void setMessage(Object message) {
            this.message = message;
        }

    }

}
