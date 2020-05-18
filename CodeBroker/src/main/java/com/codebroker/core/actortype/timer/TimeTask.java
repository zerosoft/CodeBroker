package com.codebroker.core.actortype.timer;

import akka.actor.typed.ActorRef;

import java.time.Duration;

public interface TimeTask {
	Object getKey();
	boolean isOnce();
	long getDelay();
	ActorRef getTarget();
	Duration getInterval();
}
