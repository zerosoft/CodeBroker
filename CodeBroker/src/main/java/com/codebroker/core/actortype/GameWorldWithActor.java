package com.codebroker.core.actortype;

import akka.actor.typed.*;
import akka.actor.typed.javadsl.*;
import com.codebroker.api.IGameUser;
import com.codebroker.api.IGameWorld;
import com.codebroker.core.ContextResolver;
import com.codebroker.core.actortype.message.IGameWorldMessage;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public class GameWorldWithActor implements IGameWorld {

	private ActorRef<IGameWorldMessage> gameWorldActorRef;
	private String name;

	public GameWorldWithActor(String name, ActorRef<IGameWorldMessage> gameWorldActorRef ) {
		this.gameWorldActorRef = gameWorldActorRef;
		this.name = name;
	}

	@Override
	public Optional<IGameUser> findIGameUserById(String id) {
		Scheduler scheduler = ContextResolver.getActorSystem().scheduler();
		CompletionStage<IGameWorldMessage.Reply> result =
				AskPattern.ask(
						gameWorldActorRef,
						replyTo -> new IGameWorldMessage.findIGameUserByIdMessage(id, replyTo),
						Duration.ofMillis(500),
						scheduler);
		CompletionStage<IGameUser> handle = result.handle((a, throwable) -> {
			if (a instanceof IGameWorldMessage.FindGameUser){
				return ((IGameWorldMessage.FindGameUser) a).gameUser;
			}else{
				return null;
			}
		});
		return Optional.ofNullable(handle.toCompletableFuture().join());
	}
}
