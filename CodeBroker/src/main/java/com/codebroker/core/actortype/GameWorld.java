package com.codebroker.core.actortype;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.codebroker.core.actortype.message.IGameWorld;

public class GameWorld extends AbstractBehavior<IGameWorld> {

	private long gameWorldId;

	public static Behavior<IGameWorld> create(int id) {
		Behavior<IGameWorld> setup =
				Behaviors.setup(ctx-> new GameWorld(ctx,id));
		return setup;
	}


	public GameWorld(ActorContext<IGameWorld> context, int id) {
		super(context);
		this.gameWorldId=id;
	}

	@Override
	public Receive<IGameWorld> createReceive() {
		return newReceiveBuilder()
				.build();
	}
}
