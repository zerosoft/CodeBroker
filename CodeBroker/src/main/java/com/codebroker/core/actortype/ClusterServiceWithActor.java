package com.codebroker.core.actortype;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.AskPattern;
import akka.cluster.sharding.typed.javadsl.ClusterSharding;
import akka.cluster.sharding.typed.javadsl.EntityRef;
import akka.cluster.sharding.typed.javadsl.EntityTypeKey;
import com.codebroker.core.ContextResolver;
import com.codebroker.core.actortype.message.IGameRootSystemMessage;
import com.codebroker.core.actortype.message.IServiceActor;
import com.codebroker.setting.SystemEnvironment;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import com.codebroker.api.internal.IService;
/**
 * Seveice 通过 Akka的actor 执行任务
 */
public class ClusterServiceWithActor implements IService  {

    private EntityTypeKey<IServiceActor> typeKey;
    private final ClusterSharding sharding;
    private String name;

    public ClusterServiceWithActor(String name, ClusterSharding clusterSharding ) {
        this.name = name;
        this.sharding =clusterSharding;
        this.typeKey=GameWorldWithActor.getTypeKey(name);
    }


    @Override
    public void init(Object obj) {
        EntityRef<IServiceActor> counterOne = sharding.entityRefFor(typeKey, name);
        counterOne.tell(new IServiceActor.Init(obj));
    }

    @Override
    public void destroy(Object obj) {
        EntityRef<IServiceActor> counterOne = sharding.entityRefFor(typeKey, name);
        counterOne.tell(new IServiceActor.Destroy(obj));
    }

    @Override
    public void handleMessage(Object obj) {
        EntityRef<IServiceActor> counterOne = sharding.entityRefFor(typeKey, name);
        counterOne.tell(new IServiceActor.HandleMessage(obj));
    }

    @Override
    public Object handleBackMessage(Object object) {
        ActorSystem<IGameRootSystemMessage> actorSystem = ContextResolver.getActorSystem();
        CompletionStage<IServiceActor.Reply> ask = AskPattern.askWithStatus(
                sharding.entityRefFor(typeKey, name),
                replyActorRef -> new IServiceActor.HandleUserMessage(object, replyActorRef),
                Duration.ofMillis(SystemEnvironment.TIME_OUT_MILLIS),
                actorSystem.scheduler());

        ask.exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
        IServiceActor.Reply join = ask.toCompletableFuture().join();
        if (join instanceof IServiceActor.HandleUserMessageBack){
            IServiceActor.HandleUserMessageBack result= (IServiceActor.HandleUserMessageBack) join;
            return Optional.of(result.object);
        }else {
            return Optional.empty();
        }
    }

    @Override
    public String getName() {
        return name;
    }

}
