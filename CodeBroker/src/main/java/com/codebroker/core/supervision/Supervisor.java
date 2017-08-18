package com.codebroker.core.supervision;

import static akka.actor.SupervisorStrategy.escalate;
import static akka.actor.SupervisorStrategy.restart;
import static akka.actor.SupervisorStrategy.resume;
import static akka.actor.SupervisorStrategy.stop;

import akka.actor.AbstractActor;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.japi.pf.DeciderBuilder;
import scala.concurrent.duration.Duration;

public class Supervisor extends AbstractActor {

	private static SupervisorStrategy strategy = new OneForOneStrategy(10, Duration.create("1 minute"),
			DeciderBuilder.match(ArithmeticException.class, e -> resume())
					.match(NullPointerException.class, e -> restart())
					.match(IllegalArgumentException.class, e -> stop()).matchAny(o -> escalate()).build());

	@Override
	public SupervisorStrategy supervisorStrategy() {
		return strategy;
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder().match(Props.class, props -> {
			getSender().tell(getContext().actorOf(props), getSelf());
		}).build();
	}
}
