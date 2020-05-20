package com.codebroker.core;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.AskPattern;
import com.codebroker.api.internal.IService;
import com.codebroker.api.internal.ManagerLocator;
import com.codebroker.core.actortype.message.IWorldMessage;

import java.time.Duration;
import java.util.concurrent.CompletionStage;


/**
 * 开放给逻辑层的内容管理器.
 *
 * @author LongJu
 */
class ManagerLocatorImpl implements ManagerLocator {

    @Override
    public <T> T getManager(Class<T> type) {
        return ContextResolver.getManager(type);
    }

    @Override
    public boolean setManager(IService type) {
        ActorSystem<IWorldMessage> actorSystem = ContextResolver.getActorSystem();
        CompletionStage<IWorldMessage.Reply> ask = AskPattern.ask(actorSystem,
                replyActorRef -> new IWorldMessage.CreateService(type.getClass().getName(), type,replyActorRef),
                Duration.ofMillis(500),
                actorSystem.scheduler());
        CompletionStage<IWorldMessage.Reply> exceptionally = ask.exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
        IWorldMessage.Reply reply = exceptionally.toCompletableFuture().join();
        return reply!=null;
    }

}
