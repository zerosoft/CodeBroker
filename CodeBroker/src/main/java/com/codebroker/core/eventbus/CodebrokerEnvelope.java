package com.codebroker.core.eventbus;

import com.codebroker.core.eventbus.CodebrokerEnvelope.MsgEnvelope;

import akka.actor.ActorRef;
import akka.event.japi.LookupEventBus;

public class CodebrokerEnvelope extends LookupEventBus<MsgEnvelope, ActorRef, String> {
	
	public static class MsgEnvelope {
	    public final String topic;//主题
	    public final Object payload;//内容

	    public MsgEnvelope(String topic, Object payload) {
	      this.topic = topic;
	      this.payload = payload;
	    }
	  }

	@Override
	public String classify(MsgEnvelope msgEnvelope) {
		return msgEnvelope.topic;
	}

	@Override
	public int compareSubscribers(ActorRef actorRef, ActorRef actorRef2) {
		return actorRef.compareTo(actorRef2);
	}

	@Override
	public int mapSize() {
		return 128;
	}

	@Override
	public void publish(MsgEnvelope event, ActorRef subscriber) {
		 subscriber.tell(event.payload, ActorRef.noSender());
		
	}
}
