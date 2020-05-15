package com.codebroker.core.entities;


import akka.actor.typed.ActorRef;
import com.codebroker.api.IGameUser;
import com.codebroker.api.event.Event;
import com.codebroker.api.event.IEvent;
import com.codebroker.api.event.IGameUserEventListener;
import com.codebroker.api.internal.ByteArrayPacket;
import com.codebroker.api.internal.IEventHandler;
import com.codebroker.core.actortype.message.IUser;
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
public class GameUser implements IGameUser , IEventHandler {

    private ActorRef<IUser> actorRef;
    private String uid;
    private Map<String, Set<IGameUserEventListener>> eventListenerMap= Maps.newTreeMap();


    public GameUser(String uid, ActorRef<IUser> spawn) {
        this.uid=uid;
        this.actorRef=spawn;
    }

    public String getUserId() {
        return uid;
    }


    @Override
    public void sendMessageToIoSession(int requestId, Object message) {
        if (message instanceof byte[]){
            ByteArrayPacket byteArrayPacket = new BaseByteArrayPacket(requestId, (byte[]) message);
            actorRef.tell(new IUser.SendMessageToSession(byteArrayPacket));
        }else if (message instanceof String){
            ByteArrayPacket byteArrayPacket = new BaseByteArrayPacket(requestId, ((String) message).getBytes());
            actorRef.tell(new IUser.SendMessageToSession(byteArrayPacket));
        }
    }

    @Override
    public void disconnect() {
        actorRef.tell(new IUser.Disconnect(true));
    }

    @Override
    public boolean isConnected() {
            return false;
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
        if (listeners != null && listeners.size() > 0){
            found = true;
        }
        return found;
    }

    public void removeEventListener(String eventType, IGameUserEventListener listener) {
        Set<IGameUserEventListener> listeners = this.eventListenerMap.get(eventType);
        if (listeners != null){
            listeners.remove(listener);
        }
    }

    public void dispatchEvent(IEvent event) {
       actorRef.tell(new IUser.LogicEvent(event));
    }

    @Override
    public void handlerEvent(IEvent event) {
        Set<IGameUserEventListener> listeners = this.eventListenerMap.get(event.getTopic());
        if (listeners != null && listeners.size() > 0){
            for (IGameUserEventListener listenerObj : listeners){
                listenerObj.handleEvent(this,event);
            }
        }
    }
}
