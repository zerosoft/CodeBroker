package com.codebroker.core;

import akka.actor.typed.ActorSystem;
import com.codebroker.api.internal.IService;
import com.codebroker.api.internal.ManagerLocator;
import com.codebroker.core.actortype.message.IWorldMessage;


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
    public void setManager(IService type) {
        ActorSystem<IWorldMessage> actorSystem = ContextResolver.getActorSystem();
        actorSystem.tell(new IWorldMessage.CreateService(type.getClass().getName(),type));
    }

}
