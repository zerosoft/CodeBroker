package com.codebroker.core.actortype;

import akka.actor.typed.*;
import akka.actor.typed.javadsl.*;
import akka.cluster.sharding.ShardCoordinator;
import akka.cluster.sharding.external.ExternalShardAllocation;
import akka.cluster.sharding.typed.ShardingEnvelope;
import akka.cluster.sharding.typed.javadsl.ClusterSharding;
import akka.cluster.sharding.typed.javadsl.Entity;
import akka.cluster.sharding.typed.javadsl.EntityRef;
import akka.cluster.sharding.typed.javadsl.EntityTypeKey;
import akka.cluster.typed.ClusterSingleton;
import akka.cluster.typed.SingletonActor;
import com.codebroker.api.IGameUser;
import com.codebroker.api.IGameWorld;
import com.codebroker.api.annotation.IServerType;
import com.codebroker.api.event.IEvent;
import com.codebroker.api.internal.IService;
import com.codebroker.core.ContextResolver;
import com.codebroker.core.actortype.message.IGameWorldMessage;
import com.codebroker.core.actortype.message.IGameRootSystemMessage;
import com.codebroker.core.data.CObjectLite;
import com.codebroker.core.data.IObject;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

/**
 * 游戏世界的Actor，代理GameWorld的事件
 */
public class GameWorldWithActor implements IGameWorld {

	public static final int TIME_OUT_MILLIS = 500;
	private ActorRef<IGameWorldMessage> gameWorldActorRef;

	public static EntityTypeKey<com.codebroker.core.actortype.message.IService> getTypeKey(String name){
		return EntityTypeKey.create(com.codebroker.core.actortype.message.IService.class,name);
	}

	private String gameWorldId;

	public GameWorldWithActor(String gameWorldId, ActorRef<IGameWorldMessage> gameWorldActorRef ) {
		this.gameWorldActorRef = gameWorldActorRef;
		this.gameWorldId = gameWorldId;
	}

	@Override
	public Optional<IGameUser> findIGameUserById(String id) {
		Scheduler scheduler = ContextResolver.getActorSystem().scheduler();
		CompletionStage<IGameWorldMessage.Reply> result =
				AskPattern.ask(
						gameWorldActorRef,
						replyTo -> new IGameWorldMessage.findIGameUserByIdMessage(id, replyTo),
						Duration.ofMillis(TIME_OUT_MILLIS),
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
			ActorSystem<IGameRootSystemMessage> actorSystem = ContextResolver.getActorSystem();
			if (!annotation.cluster()){
				ClusterSingleton singleton = ClusterSingleton.get(actorSystem);
				ActorRef<com.codebroker.core.actortype.message.IService> serviceActorRef = singleton.init(SingletonActor.of(ServiceActor.create(serviceName, service), serviceName));
				ServiceWithActor serviceActor=new ServiceWithActor(serviceName,serviceActorRef);

				new ObjectActorDecorate<>(serviceActor, service).newProxyInstance(service.getClass());

				ActorPathService.localService.put(serviceName,serviceActorRef);
				ContextResolver.setManager(service);
				return true;
			}else{
				ClusterSharding clusterSharding = ClusterSharding.get(actorSystem);
				EntityTypeKey<com.codebroker.core.actortype.message.IService> typeKey = getTypeKey(serviceName);
				ActorRef<ShardingEnvelope<com.codebroker.core.actortype.message.IService>> shardRegion =
						clusterSharding.init(Entity.of(
								typeKey,
//							com.codebroker.core.actortype.message.IService.typeKey,
								ctx -> {
									String ctxEntityId = ctx.getEntityId();

									Behavior<com.codebroker.core.actortype.message.IService> commandBehavior =
											ClusterServiceActor.create(ctxEntityId,service);
									return commandBehavior;
								}));
				ClusterServiceWithActor serviceActor=new ClusterServiceWithActor(serviceName,clusterSharding);

				new ObjectActorDecorate<>(serviceActor, service).newProxyInstance(service.getClass());

				ActorPathService.localClusterService.put(serviceName,shardRegion);
				ContextResolver.setManager(service);
				return true;
			}
		}else {
			ActorSystem<IGameRootSystemMessage> actorSystem = ContextResolver.getActorSystem();
			CompletionStage<IGameRootSystemMessage.Reply> ask = AskPattern.ask(actorSystem,
					replyActorRef -> new IGameRootSystemMessage.createGlobalService(serviceName, service,replyActorRef),
					Duration.ofMillis(TIME_OUT_MILLIS),
					actorSystem.scheduler());
			CompletionStage<IGameRootSystemMessage.Reply> exceptionally = ask.whenComplete((reply, throwable) -> {
				if (reply instanceof IGameRootSystemMessage.ReplyCreateService) {
					ActorPathService.localService.put(serviceName, ((IGameRootSystemMessage.ReplyCreateService) reply).serviceActorRef);
				}
			}).exceptionally(throwable -> {
				throwable.printStackTrace();
				return null;
			});
			IGameRootSystemMessage.Reply reply = exceptionally.toCompletableFuture().join();
			return reply!=null;
		}

	}


	@Override
	public void sendMessageToService(String serviceName, IObject object) {
		/**
		 * 如果是当前系统创建则使用当前系统的
		 */
		if (ActorPathService.localService.containsKey(serviceName)){
			ActorRef<com.codebroker.core.actortype.message.IService> iServiceActorRef = ActorPathService.localService.get(serviceName);
			iServiceActorRef.tell(new com.codebroker.core.actortype.message.IService.HandleMessage(object));
		}else if (ActorPathService.localClusterService.containsKey(serviceName)){
			ShardingEnvelope<com.codebroker.core.actortype.message.IService> shardingEnvelope =
					new ShardingEnvelope<>(serviceName, new com.codebroker.core.actortype.message.IService.HandleMessage(object));
			ActorRef<ShardingEnvelope<com.codebroker.core.actortype.message.IService>> shardingEnvelopeActorRef = ActorPathService.localClusterService.get(serviceName);
			shardingEnvelopeActorRef.tell(shardingEnvelope);
		}
		else {
			gameWorldActorRef.tell(new IGameWorldMessage.SendMessageToService(serviceName,object));
		}

	}

	@Override
	public IObject sendMessageToServiceWithBack(String serviceName, IObject object) {
		if (ActorPathService.localService.containsKey(serviceName)) {
			ActorSystem<IGameRootSystemMessage> actorSystem = ContextResolver.getActorSystem();
			CompletionStage<com.codebroker.core.actortype.message.IService.Reply> ask = AskPattern.askWithStatus(
					ActorPathService.localService.get(serviceName),
					replyActorRef -> new com.codebroker.core.actortype.message.IService.HandleUserMessage(object, replyActorRef),
					Duration.ofMillis(3),
					actorSystem.scheduler());
			ask.exceptionally(throwable -> {
				throwable.printStackTrace();
				return null;
			});
			com.codebroker.core.actortype.message.IService.Reply join = ask.toCompletableFuture().join();
			if (join instanceof com.codebroker.core.actortype.message.IService.HandleUserMessageBack){
				return ((com.codebroker.core.actortype.message.IService.HandleUserMessageBack) join).object;
			}else {
				return CObjectLite.newInstance();
			}
		}else
//			if (ActorPathService.localClusterService.containsKey(serviceName))
		{
			ActorSystem<IGameRootSystemMessage> actorSystem = ContextResolver.getActorSystem();

//			EntityTypeKey<com.codebroker.core.actortype.message.IService> typeKey =
//					EntityTypeKey.create(com.codebroker.core.actortype.message.IService.class,serviceName);

			ClusterSharding sharding = ClusterSharding.get(actorSystem);
			EntityRef<com.codebroker.core.actortype.message.IService> entityRef =sharding.entityRefFor(
					getTypeKey(serviceName),
//					com.codebroker.core.actortype.message.IService.typeKey,
					serviceName);

			CompletionStage<com.codebroker.core.actortype.message.IService.Reply> result = AskPattern.askWithStatus(
					entityRef,
					replyActorRef ->  new com.codebroker.core.actortype.message.IService.HandleUserMessage(object, replyActorRef),
					Duration.ofSeconds(5),
					actorSystem.scheduler());

			result.exceptionally(throwable -> {
				throwable.printStackTrace();
				return null;
			});
			com.codebroker.core.actortype.message.IService.Reply join = result.toCompletableFuture().join();
			if (join instanceof com.codebroker.core.actortype.message.IService.HandleUserMessageBack){
				return ((com.codebroker.core.actortype.message.IService.HandleUserMessageBack) join).object;
			}else {
				return CObjectLite.newInstance();
			}
		}
//		return null;
	}

	@Override
	public void sendAllOnlineUserMessage(int requestId, Object message) {
		gameWorldActorRef.tell(new IGameWorldMessage.SendAllOnlineUserMessage(requestId,message));
	}

	@Override
	public void sendAllOnlineUserEvent(IEvent event) {
		gameWorldActorRef.tell(new IGameWorldMessage.SendAllOnlineUserEvent(event));
	}

	@Override
	public void restart() {
		com.codebroker.core.actortype.message.IService.Destroy destroy = new com.codebroker.core.actortype.message.IService.Destroy("");
		ActorPathService.localService.values().forEach(iServiceActorRef -> iServiceActorRef.tell(destroy));
		for (Map.Entry<String, ActorRef<ShardingEnvelope<com.codebroker.core.actortype.message.IService>>> stringActorRefEntry : ActorPathService.localClusterService.entrySet()) {
			stringActorRefEntry.getValue().tell(new ShardingEnvelope<>(stringActorRefEntry.getKey(), destroy));
		}
	}
}
