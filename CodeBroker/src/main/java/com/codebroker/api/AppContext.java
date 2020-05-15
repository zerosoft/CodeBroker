package com.codebroker.api;

import akka.actor.typed.ActorSystem;
import com.codebroker.api.internal.IService;
import com.codebroker.api.internal.InternalContext;
import com.codebroker.core.actortype.message.IWorldMessage;
import com.codebroker.exception.ManagerNotFoundException;


/**
 * 引擎应用的上下文.
 *
 * @author LongJu
 */
public final class AppContext {

    /**
     * Instantiates a new app context.
     */
    private AppContext() {
    }

    /**
     * Gets the manager.
     *
     * @param <T>  the generic type
     * @param type the type
     * @return the manager
     */
    public static <T> T getManager(Class<T> type) {
        try {
            return InternalContext.getManagerLocator().getManager(type);
        } catch (IllegalStateException ise) {
            throw new ManagerNotFoundException("ManagerLocator is " + "unavailable", ise);
        }
    }

    /**
     * Sets the manager.
     *
     * @param service the new manager
     */
    public static void setManager(IService service) {
        InternalContext.getManagerLocator().setManager(service);
    }

    /**
     * Gets the actor system.
     *
     * @return the actor system
     */
    public static ActorSystem<IWorldMessage> getGameWorld() {
        try {
            return InternalContext.getManagerLocator().getActorSystem();
        } catch (IllegalStateException ise) {
            throw new ManagerNotFoundException("ManagerLocator is " + "unavailable", ise);
        }
    }


}
