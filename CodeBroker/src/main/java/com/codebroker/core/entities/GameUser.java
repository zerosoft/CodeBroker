package com.codebroker.core.entities;


import akka.actor.typed.ActorRef;
import com.codebroker.api.AppContext;
import com.codebroker.api.IGameUser;
import com.codebroker.api.event.IEvent;
import com.codebroker.api.event.IEventDispatcher;
import com.codebroker.api.event.IGameUserEventListener;
import com.codebroker.api.internal.ByteArrayPacket;
import com.codebroker.api.event.IEventHandler;
import com.codebroker.api.internal.IPacket;
import com.codebroker.api.internal.IResultStatusMessage;
import com.codebroker.core.actortype.message.IUserActor;
import com.codebroker.protocol.BaseByteArrayPacket;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 操作代理对象
 *
 * @author LongJu
 */
public class GameUser implements IGameUser, IEventDispatcher<String>, IEventHandler {

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
    public void sendMessageToSelf(String userId, IPacket message) {
        actorRef.tell(new IUserActor.SendMessageToGameUserActor(userId, message));
    }

    @Override
    public void sendMessageToSelf(IPacket message) {
        actorRef.tell(new IUserActor.GetSendMessageToGameUserActor(message));
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

    @Override
    public void sendEventToSelf(IEvent event) {
        actorRef.tell(new IUserActor.LogicEvent(event));
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
