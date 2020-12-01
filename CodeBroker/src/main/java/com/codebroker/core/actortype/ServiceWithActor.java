package com.codebroker.core.actortype;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorRefResolver;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.AskPattern;
import com.codebroker.api.internal.*;
import com.codebroker.core.ContextResolver;
import com.codebroker.core.actortype.message.IGameRootSystemMessage;
import com.codebroker.core.actortype.message.IServiceActor;
import com.codebroker.core.actortype.message.IUserActor;
import com.codebroker.extensions.service.ResultStatusMessage;
import com.codebroker.setting.SystemEnvironment;

import java.io.Serializable;
import java.time.Duration;
import java.util.concurrent.CompletionStage;

/**
 * Seveice 通过 Akka的actor 执行任务
 */
public class ServiceWithActor implements IService<IPacket,IResultStatusMessage>
{

    private transient ActorRef<IServiceActor> actorActorRef;

    private String name;
    //Actor 序列化地址
    private String actorRefStringPath;

    public ServiceWithActor(String name, ActorRef<IServiceActor> actorActorRef ) {
        this.actorActorRef = actorActorRef;
        this.name = name;
        ActorSystem<IGameRootSystemMessage> actorSystem = ContextResolver.getActorSystem();
        this.actorRefStringPath = ActorRefResolver.get(actorSystem).toSerializationFormat(actorActorRef);
    }

    public String getActorRefStringPath() {
        return actorRefStringPath;
    }


    public ActorRef<IServiceActor> getActorRef() {
        if (actorActorRef==null){
            ActorSystem<IGameRootSystemMessage> actorSystem = ContextResolver.getActorSystem();
            ActorRef<IServiceActor> result= ActorRefResolver.get(actorSystem).resolveActorRef(actorRefStringPath);
            actorActorRef=result;
        }
        return actorActorRef;
    }
    @Override
    public void init(Object obj) {
        getActorRef().tell(new IServiceActor.Init(obj));
    }

    @Override
    public void destroy(Object obj) {
        getActorRef().tell(new IServiceActor.Destroy(obj));
    }

    @Override
    public void handleMessage(IPacket obj) {
        getActorRef().tell(new IServiceActor.HandleMessage(obj));
    }

    @Override
    public IResultStatusMessage handleBackMessage(IPacket object) {
        ActorSystem<IGameRootSystemMessage> actorSystem = ContextResolver.getActorSystem();
        CompletionStage<IServiceActor.Reply> ask = AskPattern.askWithStatus(
                getActorRef(),
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
            return new ResultStatusMessage(result.object);
        }else {
            return new ResultStatusMessage();
        }
    }

    @Override
    public String getName() {
        return getActorRef().path().toString();
    }


}
