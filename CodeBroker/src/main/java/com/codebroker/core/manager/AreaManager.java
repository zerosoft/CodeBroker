package com.codebroker.core.manager;

import akka.actor.ActorRef;
import com.codebroker.api.IArea;
import com.codebroker.api.manager.IAreaManager;
import com.codebroker.core.ContextResolver;
import com.codebroker.core.actor.AreaManagerActor;
import com.codebroker.core.entities.Area;
import com.codebroker.protocol.ThriftSerializerFactory;
import com.codebroker.util.AkkaUtil;
import com.message.thrift.actor.Operation;
import com.message.thrift.actor.areamanager.CreateArea;
import com.message.thrift.actor.areamanager.RemoveArea;

import java.util.List;

/**
 * 区域管理器
 *
 * @author ZERO
 */
public class AreaManager implements IAreaManager {

    ThriftSerializerFactory thriftSerializerFactory = new ThriftSerializerFactory();

    @Override
    public void createArea(int loaclAreaId) {
        CreateArea message = new CreateArea(loaclAreaId);
        byte[] actorMessageWithSubClass = thriftSerializerFactory.getActorMessageByteArray(Operation.AREA_MANAGER_CREATE_AREA, message);
        CacheManager component = ContextResolver.getComponent(CacheManager.class);
        ActorRef localPath = component.getActorGlobalPath(AreaManagerActor.IDENTIFY);
        AkkaUtil.getInbox().send(localPath, actorMessageWithSubClass);
    }

    @Override
    public IArea getAreaById(int loaclAreaId) {
        CacheManager component = ContextResolver.getComponent(CacheManager.class);
        ActorRef localPath = component.getAreaLocalPaths(loaclAreaId);
        return new Area(localPath);
    }

    @Override
    public void removeArea(int loaclAreaId) {
        RemoveArea message = new RemoveArea(loaclAreaId);
        byte[] actorMessageWithSubClass = thriftSerializerFactory.getActorMessageByteArray(Operation.AREA_MANAGER_REMOVE_AREA, message);
        CacheManager component = ContextResolver.getComponent(CacheManager.class);
        ActorRef localPath = component.getActorGlobalPath(AreaManagerActor.IDENTIFY);
        localPath.tell(actorMessageWithSubClass, ActorRef.noSender());
    }

    @Override
    public List<ActorRef> getAllArea() {
        CacheManager component = ContextResolver.getComponent(CacheManager.class);
        return component.getAreaLocalPaths();
    }

    @Override
    public List<String> getAllAreaIds() {
        CacheManager component = ContextResolver.getComponent(CacheManager.class);
        return component.getAreaLocalIds();
    }


}
