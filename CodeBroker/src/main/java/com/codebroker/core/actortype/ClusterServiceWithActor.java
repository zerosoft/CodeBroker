package com.codebroker.core.actortype;

import akka.actor.typed.ActorRef;
import akka.actor.typed.javadsl.ActorContext;
import akka.cluster.sharding.typed.ShardingEnvelope;
import akka.cluster.sharding.typed.javadsl.ClusterSharding;
import akka.cluster.sharding.typed.javadsl.EntityRef;
import akka.cluster.sharding.typed.javadsl.EntityTypeKey;
import com.codebroker.core.actortype.message.IService;
import com.codebroker.core.data.IObject;

/**
 * Seveice 通过 Akka的actor 执行任务
 */
public class ClusterServiceWithActor implements com.codebroker.api.internal.IService {

    private EntityTypeKey<IService> typeKey;
    private final ClusterSharding sharding;
    private String name;

    public ClusterServiceWithActor(String name, ClusterSharding clusterSharding ) {
        this.name = name;
        this.sharding =clusterSharding;
        this.typeKey= EntityTypeKey.create(IService.class, name);
    }


    @Override
    public void init(Object obj) {
        EntityRef<IService> counterOne = sharding.entityRefFor(typeKey, name);
        counterOne.tell(new IService.Init(obj));
    }

    @Override
    public void destroy(Object obj) {
        EntityRef<IService> counterOne = sharding.entityRefFor(typeKey, name);
        counterOne.tell(new IService.Destroy(obj));
    }

    @Override
    public void handleMessage(IObject obj) {
        EntityRef<IService> counterOne = sharding.entityRefFor(typeKey, name);
        counterOne.tell(new IService.HandleMessage(obj));
    }

    @Override
    public String getName() {
        return name;
    }

}
