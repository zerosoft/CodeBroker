package com.codebroker.core.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;
import akka.serialization.Serialization;
import com.codebroker.core.ContextResolver;
import com.codebroker.core.ServerEngine;
import com.codebroker.core.cluster.ClusterDistributedSub.Subscribe;
import com.codebroker.core.manager.CacheManager;
import com.codebroker.protocol.ThriftSerializerFactory;
import com.message.thrift.actor.ActorMessage;
import com.message.thrift.actor.areamanager.CreateArea;
import com.message.thrift.actor.areamanager.RemoveArea;

/**
 * 区域管理器
 * 创建销毁区域Area
 */
public class AreaManagerActor extends AbstractActor {

    public static final String IDENTIFY = AreaManagerActor.class.getSimpleName();

    ThriftSerializerFactory thriftSerializerFactory = new ThriftSerializerFactory();
    ActorRef mediator;




    @Override
    public void preStart() throws Exception {
        super.preStart();
        mediator = DistributedPubSub.get(getContext().system()).mediator();
        mediator.tell(new DistributedPubSubMediator.Subscribe(IDENTIFY, getSelf()), getSelf());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(byte[].class, msg -> {
                    ActorMessage actorMessage = thriftSerializerFactory.getActorMessage(msg);
                    switch (actorMessage.op) {
                        case AREA_MANAGER_CREATE_AREA:
                            CreateArea createArea = new CreateArea();
                            thriftSerializerFactory.deserialize(createArea, actorMessage.messageRaw);
                            createArea(createArea.areaId);
                            break;
                        case AREA_MANAGER_REMOVE_AREA:
                            RemoveArea removeArea = new RemoveArea();
                            thriftSerializerFactory.deserialize(removeArea, actorMessage.messageRaw);
                            removeAreaById(removeArea.areaId);
                            break;
                        default:
                            break;
                    }
                })
                .match(Subscribe.class, msg -> {
                    mediator.tell(new DistributedPubSubMediator.Subscribe(IDENTIFY, getSelf()), getSelf());
                }).matchAny(msg -> {
                    System.out.println(msg);
                }).build();
    }


    private void createArea(int loaclGridId) {
        CacheManager cacheManager = ContextResolver.getComponent(CacheManager.class);
        if (cacheManager.containsAreaKey(loaclGridId)) {
            return;
        } else {
            ActorRef actorOf = getContext().actorOf(Props.create(AreaActor.class, CacheManager.getAreaId(loaclGridId)), CacheManager.getAreaId(loaclGridId));

            getContext().watch(actorOf);

            String identifier = Serialization.serializedActorPath(actorOf);
            cacheManager.setLocalAreaPath(CacheManager.getAreaId(loaclGridId), identifier);
        }
    }

    private void removeAreaById(int loaclAreaId) {
        String key = "SERVER_" + ServerEngine.serverId + ":AREA_" + loaclAreaId;
        CacheManager cacheManager = ContextResolver.getComponent(CacheManager.class);
        cacheManager.removeAreaActorRefPath(key);
    }


}
