package com.codebroker.net.http;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.codebroker.core.actortype.message.IService;

public class HttpClientActor extends AbstractBehavior<IService> {



	public static Behavior<IService> create(String shardId, int httpPort) {
		return Behaviors.setup(context ->
				Behaviors.withTimers(timers ->
						new HttpClientActor(context)
				)
		);
	}

	public HttpClientActor(ActorContext<IService> context) {
		super(context);
	}

	@Override
	public Receive<IService> createReceive() {
		return null;
	}
}
