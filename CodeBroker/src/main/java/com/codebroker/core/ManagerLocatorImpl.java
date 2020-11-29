package com.codebroker.core;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.AskPattern;
import com.codebroker.api.IGameWorld;
import com.codebroker.api.internal.IService;
import com.codebroker.api.internal.ManagerLocator;
import com.codebroker.core.actortype.message.IGameRootSystemMessage;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletionStage;


/**
 * 开放给逻辑层的内容管理器.
 *
 * @author LongJu
 */
class ManagerLocatorImpl implements ManagerLocator {

    @Override
    public <T> Optional<T> getManager(Class<T> type) {
        return ContextResolver.getManager(type);
    }

    @Override
    public boolean setManager(IService  type) {
        ActorSystem<IGameRootSystemMessage> actorSystem = ContextResolver.getActorSystem();
        CompletionStage<IGameRootSystemMessage.Reply> ask = AskPattern
                .ask(actorSystem,
                replyActorRef -> new IGameRootSystemMessage.CreateService(type.getClass().getName(), type,replyActorRef),
                Duration.ofSeconds(1),
                actorSystem.scheduler());
        CompletionStage<IGameRootSystemMessage.Reply> exceptionally = ask.exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
        IGameRootSystemMessage.Reply reply = exceptionally.toCompletableFuture().join();
        return reply!=null;
    }

    @Override
    public IGameWorld getGameWorld() {
       return ContextResolver.getGameWorld();
    }

    @Override
    public <T> Optional<T> getComponent(Class<T> type) {
        return ContextResolver.getComponent(type);
    }

    @Override
    public void setComponent(IService  service) {
        ContextResolver.setComponent(service);
    }

}
