package com.codebroker.core.entities;


import akka.actor.typed.ActorRef;
import com.codebroker.api.IGameUser;
import com.codebroker.api.event.IEvent;
import com.codebroker.api.event.IGameUserEventListener;
import com.codebroker.api.internal.ByteArrayPacket;
import com.codebroker.api.internal.IEventHandler;
import com.codebroker.core.actortype.message.IUser;
import com.codebroker.protocol.BaseByteArrayPacket;
import com.codebroker.protocol.SerializableType;

/**
 * 操作代理对象
 *
 * @author LongJu
 */
public class GameUserProxy implements IGameUser , IEventHandler, SerializableType {

    private ActorRef<IUser> actorRef;

    public ActorRef<IUser> getActorRef() {
        return actorRef;
    }

    public GameUserProxy(ActorRef<IUser> spawn) {
        this.actorRef=spawn;
    }

    public String getUserId() {
        return "uid";
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
    }

    public boolean hasEventListener(String eventType) {
        return false;
    }

    public void removeEventListener(String eventType, IGameUserEventListener listener) {

    }

    public void dispatchEvent(IEvent event) {
       actorRef.tell(new IUser.LogicEvent(event));
    }

    @Override
    public void handlerEvent(IEvent event) {
//        Set<IGameUserEventListener> listeners = this.eventListenerMap.get(event.getTopic());
//        if (listeners != null && listeners.size() > 0){
//            for (IGameUserEventListener listenerObj : listeners){
//                listenerObj.handleEvent(this,event);
//            }
//        }
    }
}
