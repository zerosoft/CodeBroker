package com.codebroker.core.entities;


import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorRefResolver;
import akka.actor.typed.ActorSystem;
import com.codebroker.api.AppContext;
import com.codebroker.api.IGameUser;
import com.codebroker.api.event.IEvent;
import com.codebroker.api.event.IEventDispatcher;
import com.codebroker.api.event.IGameUserEventListener;
import com.codebroker.api.internal.ByteArrayPacket;
import com.codebroker.api.event.IEventHandler;
import com.codebroker.api.internal.IPacket;
import com.codebroker.api.internal.IResultStatusMessage;
import com.codebroker.core.ContextResolver;
import com.codebroker.core.actortype.message.IGameRootSystemMessage;
import com.codebroker.core.actortype.message.IUserActor;
import com.codebroker.protocol.BaseByteArrayPacket;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 操作代理对象
 *
 * @author LongJu
 */
public class GameUser implements IGameUser, IEventDispatcher<String>, IEventHandler {

    private transient Logger logger = LoggerFactory.getLogger(GameUser.class);
    private transient Map<String, Set<IGameUserEventListener>> eventListenerMap = Maps.newTreeMap();
    private transient ActorRef<IUserActor> actorRef;

    private String uid;
    //Actor 序列化地址
    private String actorRefStringPath;

    public String getActorRefString(){
        return actorRefStringPath;
    }

    public ActorRef<IUserActor> getActorRef() {
        if (actorRef==null){
            ActorSystem<IGameRootSystemMessage> actorSystem = ContextResolver.getActorSystem();
            ActorRef<IUserActor> result= ActorRefResolver.get(actorSystem).resolveActorRef(actorRefStringPath);
            actorRef=result;
        }
        return actorRef;
    }

    public void clean() {
        uid = null;
        actorRefStringPath = null;
        eventListenerMap.clear();
    }

    public GameUser(String uid, ActorRef<IUserActor> spawn) {

        this.uid = uid;
        if (Objects.nonNull(spawn)){
            this.actorRef=spawn;
            ActorSystem<IGameRootSystemMessage> actorSystem = ContextResolver.getActorSystem();
            this.actorRefStringPath = ActorRefResolver.get(actorSystem).toSerializationFormat(spawn);
        }
    }


    public void setActorRef(ActorRef<IUserActor> actorRef) {
        ActorSystem<IGameRootSystemMessage> actorSystem = ContextResolver.getActorSystem();
        this.actorRefStringPath = ActorRefResolver.get(actorSystem).toSerializationFormat(actorRef);
        this.actorRef=actorRef;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserId() {
        return uid;
    }


    @Override
    public void sendMessageToIoSession(int requestId, Object message) {
        if (message instanceof byte[]) {
            ByteArrayPacket byteArrayPacket = new BaseByteArrayPacket(requestId, (byte[]) message);
            getActorRef().tell(new IUserActor.SendMessageToSession(byteArrayPacket));
        } else if (message instanceof String) {
            ByteArrayPacket byteArrayPacket = new BaseByteArrayPacket(requestId, ((String) message).getBytes());
            getActorRef().tell(new IUserActor.SendMessageToSession(byteArrayPacket));
        }
    }

    @Override
    public void sendMessageToIoSession(IPacket message) {
        getActorRef().tell(new IUserActor.SendMessageToSession(message));
    }

    @Override
    public void sendMessageToSelf(IEvent event) {
        getActorRef().tell(new IUserActor.SendEventToGameUserActor(event));
    }

    @Override
    public IResultStatusMessage sendMessageToLocalIService(String serviceName, IPacket message) {
        return AppContext.getGameWorld().sendMessageToLocalIService(serviceName, message);
    }

    @Override
    public IResultStatusMessage sendMessageToLocalIService(Class iService, IPacket message) {
        return AppContext.getGameWorld().sendMessageToLocalIService(iService, message);
    }

    @Override
    public void sendMessageToIService(String serviceName, IPacket message) {
        AppContext.getGameWorld().sendMessageToIService(serviceName, message);
    }

    @Override
    public void sendMessageToIService(Class iService, IPacket message) {
        AppContext.getGameWorld().sendMessageToIService(iService, message);
    }

    @Override
    public void sendMessageToIService(long serverId, Class iService, IPacket message) {
        AppContext.getGameWorld().sendMessageToIService(iService.getName()+"."+serverId, message);
    }

    @Override
    public void disconnect() {
        getActorRef().tell(new IUserActor.Disconnect(true));
    }

    @Override
    public boolean isConnected() {
        return actorRefStringPath != null;
    }

    @Override
    public void addEventListener(UserEvent userEvent, IGameUserEventListener iGameUserEventListener) {
        addEventListener(userEvent.name(),iGameUserEventListener);
    }

    public void rebindIoSession(ActorRef actorRef) {

    }


    public void addEventListener(String eventType, IGameUserEventListener listener) {
        Set<IGameUserEventListener> listeners = eventListenerMap.get(eventType);
        if (listeners == null) {
            listeners = new CopyOnWriteArraySet<>();
            this.eventListenerMap.put(eventType, listeners);
        }
        listeners.add(listener);
    }

    @Override
    public void sendEventToSelf(IEvent event) {
        getActorRef().tell(new IUserActor.LogicEvent(event));
    }

    public boolean hasEventListener(String eventType) {
        boolean found = false;
        Set<IGameUserEventListener> listeners = this.eventListenerMap.get(eventType);
        if (listeners != null && listeners.size() > 0) {
            found = true;
        }
        return found;
    }

    public void removeEventListener(String eventType, IGameUserEventListener listener) {
        Set<IGameUserEventListener> listeners = this.eventListenerMap.get(eventType);
        if (listeners != null) {
            listeners.remove(listener);
        }
    }

    public void dispatchEvent(IEvent event) {
        getActorRef().tell(new IUserActor.LogicEvent(event));
    }

    @Override
    public void handlerEvent(IEvent event) {
        Set<IGameUserEventListener> listeners = this.eventListenerMap.get(event.getTopic());
        if (listeners != null && listeners.size() > 0) {
            for (IGameUserEventListener listenerObj : listeners) {
                try {
                    listenerObj.handleEvent(this, event.getMessage());
                }catch (Exception e){
                    logger.error("event error",e);
                }
            }
        }
    }
}
