package com.codebroker.core.actortype;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.codebroker.api.IGameUser;
import com.codebroker.core.actortype.message.IGameWorldMessage;

import java.util.Map;

public class GameWorld extends AbstractBehavior<IGameWorldMessage> {

	private long gameWorldId;
	Map<String, IGameUser> userMap;

	public static Behavior<IGameWorldMessage> create(int id) {
		Behavior<IGameWorldMessage> setup =
				Behaviors.setup(ctx-> new GameWorld(ctx,id));
		return setup;
	}


	public GameWorld(ActorContext<IGameWorldMessage> context, int id) {
		super(context);
		this.gameWorldId=id;
	}

	@Override
	public Receive<IGameWorldMessage> createReceive() {
		return newReceiveBuilder()
				.onMessage(IGameWorldMessage.findIGameUserByIdMessage.class,this::findGameUserById)
				.build();
	}

	private Behavior<IGameWorldMessage> findGameUserById(IGameWorldMessage.findIGameUserByIdMessage message) {

		return Behaviors.same();
	}
}
