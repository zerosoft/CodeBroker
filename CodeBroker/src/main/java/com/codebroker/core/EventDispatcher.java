package com.codebroker.core;

import akka.actor.ActorRef;
import com.codebroker.api.event.Event;
import com.codebroker.api.event.IEventDispatcher;
import com.codebroker.api.event.IEventListener;
import com.codebroker.api.event.event.AddEventListener;
import com.codebroker.api.event.event.HasEventListener;
import com.codebroker.api.event.event.RemoveEventListener;
import com.codebroker.exception.NoActorRefException;
import com.codebroker.util.AkkaUtil;

/**
 * 事件分发器抽象类
 *
 * @author zero
 */
public abstract class EventDispatcher implements IEventDispatcher {

    private ActorRef actorRef;

    public ActorRef getActorRef() {
        if (actorRef == null) {
            throw new NoActorRefException();
        }
        return actorRef;
    }

    public void setActorRef(ActorRef gridRef) {
        this.actorRef = gridRef;
    }

    @Override
    public void addEventListener(String topic, IEventListener eventListener) {
        if (actorRef != null) {
            actorRef.tell(new AddEventListener(topic, eventListener), ActorRef.noSender());
        }
    }

    @Override
    public boolean hasEventListener(String paramString) {
        try {
            if (actorRef != null) {
                return AkkaUtil.getCallBak(actorRef, new HasEventListener(paramString));
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void removeEventListener(String paramString) {
        if (actorRef != null) {
            actorRef.tell(new RemoveEventListener(paramString), ActorRef.noSender());
        }
    }

    @Override
    public void dispatchEvent(Event object) {
        if (actorRef != null) {
            actorRef.tell(object, ActorRef.noSender());
        }

    }
}
