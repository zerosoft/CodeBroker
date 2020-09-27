package com.codebroker.core.entities;


import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.AskPattern;
import com.codebroker.api.IGameUser;
import com.codebroker.api.event.IEvent;
import com.codebroker.api.event.IGameUserEventListener;
import com.codebroker.api.internal.ByteArrayPacket;
import com.codebroker.api.internal.IEventHandler;
import com.codebroker.core.ContextResolver;
import com.codebroker.core.actortype.message.IUser;
import com.codebroker.core.actortype.message.IGameRootSystemMessage;
import com.codebroker.core.data.IObject;
import com.codebroker.protocol.BaseByteArrayPacket;
import com.codebroker.protocol.SerializableType;
import com.google.common.collect.Maps;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 操作代理对象
 *
 * @author LongJu
 */
public class GameUser implements IGameUser , IEventHandler, SerializableType {

    private ActorRef<IUser> actorRef;

    public ActorRef<IUser> getActorRef() {
        return actorRef;
    }

    private String uid;
    private Map<String, Set<IGameUserEventListener>> eventListenerMap= Maps.newTreeMap();

    public void clean(){
        uid=null;
        actorRef=null;
        eventListenerMap.clear();
    }

    public GameUser(String uid, ActorRef<IUser> spawn) {
        this.uid=uid;
        this.actorRef=spawn;
    }


    public void setActorRef(ActorRef<IUser> actorRef) {
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
        if (message instanceof byte[]){
            ByteArrayPacket byteArrayPacket = new BaseByteArrayPacket(requestId, (byte[]) message);
            actorRef.tell(new IUser.SendMessageToSession(byteArrayPacket));
        }else if (message instanceof String){
            ByteArrayPacket byteArrayPacket = new BaseByteArrayPacket(requestId, ((String) message).getBytes());
            actorRef.tell(new IUser.SendMessageToSession(byteArrayPacket));
        }
    }

    @Override
    public void sendMessageToGameUser(String userId, IObject message) {
        actorRef.tell(new IUser.SendMessageToGameUser(userId,message));
    }

    @Override
    public Optional<IObject> sendMessageToLocalIService(String serviceName, IObject message) {
        ActorSystem<IGameRootSystemMessage> actorSystem = ContextResolver.getActorSystem();
        CompletionStage<IUser.Reply> ask = AskPattern.ask(actorRef,
                replyActorRef -> new IUser.SendMessageToIService(serviceName, message,replyActorRef),
                Duration.ofMillis(3),
                actorSystem.scheduler());
        IUser.IObjectReply reply = (IUser.IObjectReply) ask.toCompletableFuture().join();
        return Optional.of(reply.message);
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
