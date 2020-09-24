package com.codebroker.core;

import akka.actor.typed.ActorSystem;
import com.codebroker.api.AppListener;
import com.codebroker.api.IGameWorld;
import com.codebroker.api.internal.IService;
import com.codebroker.core.actortype.message.IGameRootSystemMessage;
import com.codebroker.util.PropertiesWrapper;


/**
 * 上下文解析器.
 */
public final class ContextResolver {

    private static KernelContext context;
    private static IGameWorld gameWorld;

    private ContextResolver() {
    }

    public static PropertiesWrapper getPropertiesWrapper() {
        return context.getPropertiesWrapper();
    }

    public static AppListener getAppListener() {
        return context.getAppListener();
    }

    public static <T> T getManager(Class<T> type) {
        return context.getManager(type);
    }

    public static <T> T getComponent(Class<T> type) {
        return context.getComponent(type);
    }

    static KernelContext getContext() {
        return context;
    }

    static void setTaskState(KernelContext ctx) {
        context = ctx;
    }

    public static void setManager(IService service) {
        context.setManager(service);
    }

    public static ActorSystem<IGameRootSystemMessage> getActorSystem() {
        return context.getActorSystem();
    }


    public static IGameWorld getGameWorld() {
        return gameWorld;
    }

    public static void setGameWorld(IGameWorld gameWorld) {
        ContextResolver.gameWorld = gameWorld;
    }
}
