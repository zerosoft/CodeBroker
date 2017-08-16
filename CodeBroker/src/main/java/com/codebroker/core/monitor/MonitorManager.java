package com.codebroker.core.monitor;

import akka.actor.AbstractActor;

public class MonitorManager extends AbstractActor {

	@Override
	public Receive createReceive() {
		return receiveBuilder().build();
	}

	public static class SesseionChange {

	}
}
