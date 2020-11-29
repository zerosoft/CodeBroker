package com.codebroker.core.actortype;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.AskPattern;
import com.codebroker.api.internal.*;
import com.codebroker.core.ContextResolver;
import com.codebroker.core.actortype.message.IGameRootSystemMessage;
import com.codebroker.core.actortype.message.IServiceActor;
import com.codebroker.setting.SystemEnvironment;

import java.io.Serializable;
import java.time.Duration;
import java.util.concurrent.CompletionStage;

/**
 * Seveice 通过 Akka的actor 执行任务
 */
public class ServiceWithActor implements IService<IRequestKeyMessage,IResultStatusMessage>
{

    private ActorRef<IServiceActor> actorActorRef;
    private String name;

    public ServiceWithActor(String name, ActorRef<IServiceActor> actorActorRef ) {
        this.actorActorRef = actorActorRef;
        this.name = name;
    }

    @Override
    public void init(Object obj) {
        actorActorRef.tell(new IServiceActor.Init(obj));
    }

    @Override
    public void destroy(Object obj) {
        actorActorRef.tell(new IServiceActor.Destroy(obj));
    }

    @Override
    public void handleMessage(IRequestKeyMessage obj) {
        actorActorRef.tell(new IServiceActor.HandleMessage(obj));
    }

    @Override
    public IResultStatusMessage handleBackMessage(IRequestKeyMessage object) {
        ActorSystem<IGameRootSystemMessage> actorSystem = ContextResolver.getActorSystem();
        CompletionStage<IServiceActor.Reply> ask = AskPattern.askWithStatus(
                actorActorRef,
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
            return new ResultStatusMessage((Serializable) result.object);
        }else {
            return new ResultStatusMessage();
        }
    }

    @Override
    public String getName() {
        return actorActorRef.path().toString();
    }


}
