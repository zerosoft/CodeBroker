package com.codebroker.core.actortype;

import akka.actor.typed.*;
import akka.actor.typed.javadsl.*;
import com.codebroker.api.IGameUser;
import com.codebroker.api.IGameWorld;
import com.codebroker.api.event.IEvent;
import com.codebroker.api.internal.IService;
import com.codebroker.core.ContextResolver;
import com.codebroker.core.actortype.message.IGameWorldMessage;
import com.codebroker.core.actortype.message.IWorldMessage;
import com.codebroker.core.data.IObject;
import com.google.common.collect.Maps;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public class GameWorldWithActor implements IGameWorld {

	private ActorRef<IGameWorldMessage> gameWorldActorRef;
	private Map<String,ActorRef<com.codebroker.core.actortype.message.IService>> localService= Maps.newTreeMap();
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

	@Override
	public boolean createGlobalService(String serviceName, IService service) {
		ActorSystem<IWorldMessage> actorSystem = ContextResolver.getActorSystem();
		CompletionStage<IWorldMessage.Reply> ask = AskPattern.ask(actorSystem,
				replyActorRef -> new IWorldMessage.createGlobalService(serviceName, service,replyActorRef),
				Duration.ofMillis(500),
				actorSystem.scheduler());
		CompletionStage<IWorldMessage.Reply> exceptionally = ask.whenComplete((reply, throwable) -> {
			if (reply instanceof IWorldMessage.ReplyCreateService) {
				localService.put(serviceName, ((IWorldMessage.ReplyCreateService) reply).serviceActorRef);
			}
		}).exceptionally(throwable -> {
			throwable.printStackTrace();
			return null;
		});
		IWorldMessage.Reply reply = exceptionally.toCompletableFuture().join();
		return reply!=null;
	}

	@Override
	public void sendMessageToService(String serviceName, IObject object) {
		/**
		 * 如果是当前系统创建则使用当前系统的
		 */
		if (localService.containsKey(serviceName)){
			localService.get(serviceName).tell(new com.codebroker.core.actortype.message.IService.HandleMessage(object));
		}else {
			gameWorldActorRef.tell(new IGameWorldMessage.SendMessageToService(serviceName,object));
		}

	}

	@Override
	public void sendAllOnlineUserMessage(int requestId, Object message) {
		gameWorldActorRef.tell(new IGameWorldMessage.SendAllOnlineUserMessage(requestId,message));
	}

	@Override
	public void sendAllOnlineUserEvent(IEvent event) {
		gameWorldActorRef.tell(new IGameWorldMessage.SendAllOnlineUserEvent(event));
	}
}
