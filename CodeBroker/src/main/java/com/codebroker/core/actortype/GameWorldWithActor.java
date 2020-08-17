package com.codebroker.core.actortype;

import akka.actor.typed.*;
import akka.actor.typed.javadsl.*;
import akka.cluster.sharding.typed.ShardingEnvelope;
import akka.cluster.sharding.typed.javadsl.ClusterSharding;
import akka.cluster.sharding.typed.javadsl.Entity;
import akka.cluster.sharding.typed.javadsl.EntityRef;
import akka.cluster.sharding.typed.javadsl.EntityTypeKey;
import com.codebroker.api.IGameUser;
import com.codebroker.api.IGameWorld;
import com.codebroker.api.annotation.IServerType;
import com.codebroker.api.event.IEvent;
import com.codebroker.api.internal.IService;
import com.codebroker.cluster.base.Counter;
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
	private Map<String,ActorRef<ShardingEnvelope<com.codebroker.core.actortype.message.IService>>> localClusterService= Maps.newTreeMap();
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
		IServerType annotation = service.getClass().getAnnotation(IServerType.class);
		if (annotation!=null){
			ActorSystem<IWorldMessage> actorSystem = ContextResolver.getActorSystem();
			ClusterSharding clusterSharding = ClusterSharding.get(actorSystem);

			EntityTypeKey<com.codebroker.core.actortype.message.IService> typeKey = EntityTypeKey.create(com.codebroker.core.actortype.message.IService.class,serviceName);

			ActorRef<ShardingEnvelope<com.codebroker.core.actortype.message.IService>> shardRegion =
					clusterSharding.init(Entity.of(typeKey, ctx -> {
						String ctxEntityId = ctx.getEntityId();
						Behavior<com.codebroker.core.actortype.message.IService> commandBehavior = ClusterServiceActor.create(serviceName,service);
						return commandBehavior;
					}));

			ClusterServiceWithActor serviceActor=new ClusterServiceWithActor(serviceName,clusterSharding);
			com.codebroker.api.internal.IService iService = new ObjectActorDecorate<>(serviceActor, service).newProxyInstance(service.getClass());

			localClusterService.put(serviceName,shardRegion);
			return true;
		}else {
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

	}

//	@Override
//	public IService getClusterService(String serviceName, IService service) {
//		IServerType annotation = service.getClass().getAnnotation(IServerType.class);
//
//
//		//获得集群
//		ActorSystem<IWorldMessage> actorSystem = ContextResolver.getActorSystem();
//		ClusterSharding clusterSharding = ClusterSharding.get(actorSystem);
//
//		EntityTypeKey<com.codebroker.core.actortype.message.IService> typeKey = EntityTypeKey.create(com.codebroker.core.actortype.message.IService.class,serviceName);
//
//		ActorRef<ShardingEnvelope<com.codebroker.core.actortype.message.IService>> shardRegion =
//				clusterSharding.init(Entity.of(typeKey, ctx -> {
//					String ctxEntityId = ctx.getEntityId();
//					Behavior<com.codebroker.core.actortype.message.IService> commandBehavior = ClusterServiceActor.create(serviceName,service);
//					return commandBehavior;
//				}));
//
//		ClusterServiceWithActor serviceActor=new ClusterServiceWithActor(serviceName,clusterSharding);
//		com.codebroker.api.internal.IService iService = new ObjectActorDecorate<>(serviceActor, service).newProxyInstance(service.getClass());
//
//		localClusterService.put(serviceName,shardRegion);
//
//		return iService;
//	}

	@Override
	public void sendMessageToService(String serviceName, IObject object) {
		/**
		 * 如果是当前系统创建则使用当前系统的
		 */
		if (localService.containsKey(serviceName)){
			localService.get(serviceName).tell(new com.codebroker.core.actortype.message.IService.HandleMessage(object));
		}else if (localClusterService.containsKey(serviceName)){
			ShardingEnvelope<com.codebroker.core.actortype.message.IService> shardingEnvelope = new ShardingEnvelope<>(serviceName, new com.codebroker.core.actortype.message.IService.HandleMessage(object));
			localClusterService.get(serviceName).tell(shardingEnvelope);
		}
		else {
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
