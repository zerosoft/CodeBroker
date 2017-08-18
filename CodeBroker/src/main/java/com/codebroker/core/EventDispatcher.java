package com.codebroker.core;

import com.codebroker.api.event.IEvent;
import com.codebroker.api.event.IEventDispatcher;
import com.codebroker.api.event.IEventListener;
import com.codebroker.core.actor.AreaActor;
import com.codebroker.exception.NoActorRefException;
import com.codebroker.util.AkkaMediator;

import akka.actor.ActorRef;

/**
 * 事件分发器抽象类
 * 
 * @author zero
 *
 */
public abstract class EventDispatcher implements IEventDispatcher {

	private ActorRef actorRef;

	public void setActorRef(ActorRef gridRef) {
		this.actorRef = gridRef;
	}

	public ActorRef getActorRef() {
		if (actorRef == null) {
			throw new NoActorRefException();
		}
		return actorRef;
	}

	@Override
	public void addEventListener(String topic, IEventListener eventListener) {
		if (actorRef != null) {
			actorRef.tell(new AreaActor.AddEventListener(topic, eventListener), ActorRef.noSender());
		}
	}

	@Override
	public boolean hasEventListener(String paramString) {
		try {
			if (actorRef != null) {
				return AkkaMediator.getCallBak(actorRef, new AreaActor.HasEventListener(paramString));
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
			actorRef.tell(new AreaActor.RemoveEventListener(paramString), ActorRef.noSender());
		}
	}

	@Override
	public void dispatchEvent(IEvent paramIEvent) {
		if (actorRef != null) {
			actorRef.tell(new AreaActor.DispatchEvent(paramIEvent), ActorRef.noSender());
		}

	}
}
