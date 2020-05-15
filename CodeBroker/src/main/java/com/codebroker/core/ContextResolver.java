package com.codebroker.core;

import akka.actor.typed.ActorSystem;
import com.codebroker.api.CodeBrokerAppListener;
import com.codebroker.api.internal.IService;
import com.codebroker.core.actortype.message.IWorldMessage;
import com.codebroker.util.PropertiesWrapper;


/**
 * 上下文解析器.
 */
public final class ContextResolver {

    private static KernelContext context;

    private ContextResolver() {
    }

    public static PropertiesWrapper getPropertiesWrapper() {
        return context.getPropertiesWrapper();
    }

    public static CodeBrokerAppListener getAppListener() {
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

    public static ActorSystem<IWorldMessage> getActorSystem() {
        return context.getActorSystem();
    }


}
