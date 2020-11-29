package com.codebroker.core.entities;


import akka.actor.typed.ActorRef;
import com.codebroker.api.AppContext;
import com.codebroker.api.IGameUser;
import com.codebroker.api.event.IEvent;
import com.codebroker.api.event.IEventDispatcher;
import com.codebroker.api.event.IGameUserEventListener;
import com.codebroker.api.internal.ByteArrayPacket;
import com.codebroker.api.internal.IEventHandler;
import com.codebroker.core.actortype.message.IUserActor;
import com.codebroker.core.data.IObject;
import com.codebroker.protocol.BaseByteArrayPacket;
import com.codebroker.protocol.SerializableType;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 操作代理对象
 *
 * @author LongJu
 */
public class GameUser implements IGameUser, IEventDispatcher, IEventHandler {

    private ActorRef<IUserActor> actorRef;

    public ActorRef<IUserActor> getActorRef() {
        return actorRef;
    }

    private String uid;

    private transient Map<String, Set<IGameUserEventListener>> eventListenerMap = Maps.newTreeMap();

    public void clean() {
        uid = null;
        actorRef = null;
        eventListenerMap.clear();
    }

    public GameUser(String uid, ActorRef<IUserActor> spawn) {
        this.uid = uid;
        this.actorRef = spawn;
    }


    public void setActorRef(ActorRef<IUserActor> actorRef) {
        this.actorRef = actorRef;
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
            actorRef.tell(new IUserActor.SendMessageToSession(byteArrayPacket));
        } else if (message instanceof String) {
            ByteArrayPacket byteArrayPacket = new BaseByteArrayPacket(requestId, ((String) message).getBytes());
            actorRef.tell(new IUserActor.SendMessageToSession(byteArrayPacket));
        }
    }

    @Override
    public void sendMessageToGameUser(String userId, IObject message) {
        actorRef.tell(new IUserActor.SendMessageToGameUserActor(userId, message));
    }

    @Override
    public void sendMessageToGameUser(IObject message) {
        actorRef.tell(new IUserActor.GetSendMessageToGameUserActor(message));
    }

    @Override
    public Optional<IObject> sendMessageToLocalIService(String serviceName, IObject message) {
        return AppContext.getGameWorld().sendMessageToLocalIService(serviceName, message);
    }

    @Override
    public Optional<IObject> sendMessageToLocalIService(Class iService, IObject message) {
        return AppContext.getGameWorld().sendMessageToLocalIService(iService, message);
    }

    @Override
    public void sendMessageToIService(String serviceName, IObject message) {
        AppContext.getGameWorld().sendMessageToIService(serviceName, message);
    }

    @Override
    public void sendMessageToIService(Class iService, IObject message) {
        AppContext.getGameWorld().sendMessageToIService(iService, message);
    }

    @Override
    public void sendMessageToIService(long serverId, Class iService, IObject message) {

    }

    @Override
    public void disconnect() {
        actorRef.tell(new IUserActor.Disconnect(true));
    }

    @Override
    public boolean isConnected() {
        return actorRef != null;
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
        actorRef.tell(new IUserActor.LogicEvent(event));
    }

    @Override
    public void handlerEvent(IEvent event) {
        Set<IGameUserEventListener> listeners = this.eventListenerMap.get(event.getTopic());
        if (listeners != null && listeners.size() > 0) {
            for (IGameUserEventListener listenerObj : listeners) {
                listenerObj.handleEvent(this, event);
            }
        }
    }
}
