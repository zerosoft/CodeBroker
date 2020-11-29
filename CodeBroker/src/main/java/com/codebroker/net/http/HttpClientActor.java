package com.codebroker.net.http;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.codebroker.core.actortype.message.IServiceActor;

public class HttpClientActor extends AbstractBehavior<IServiceActor> {



	public static Behavior<IServiceActor> create(String shardId, int httpPort) {
		return Behaviors.setup(context ->
				Behaviors.withTimers(timers ->
						new HttpClientActor(context)
				)
		);
	}

	public HttpClientActor(ActorContext<IServiceActor> context) {
		super(context);
	}

	@Override
	public Receive<IServiceActor> createReceive() {
		return null;
	}
}
