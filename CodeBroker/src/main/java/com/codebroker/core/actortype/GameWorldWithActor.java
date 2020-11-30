package com.codebroker.core.actortype;

import akka.actor.typed.*;
import akka.actor.typed.javadsl.*;
import akka.cluster.sharding.typed.ShardingEnvelope;
import akka.cluster.sharding.typed.javadsl.ClusterSharding;
import akka.cluster.sharding.typed.javadsl.Entity;
import akka.cluster.sharding.typed.javadsl.EntityTypeKey;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.ContentTypes;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.unmarshalling.Unmarshaller;
import akka.stream.SystemMaterializer;
import com.codebroker.api.IGameUser;
import com.codebroker.api.IGameWorld;
import com.codebroker.api.event.IEvent;
import com.codebroker.api.internal.IPacket;
import com.codebroker.api.internal.IResultStatusMessage;
import com.codebroker.api.internal.IService;
import com.codebroker.component.service.ZookeeperComponent;
import com.codebroker.core.ContextResolver;
import com.codebroker.core.ServerEngine;
import com.codebroker.core.actortype.message.IGameWorldActor;
import com.codebroker.core.actortype.message.IGameRootSystemMessage;
import com.codebroker.core.actortype.message.IServiceActor;
import com.codebroker.extensions.service.ResultStatusMessage;
import com.codebroker.net.http.HTTPRequest;
import com.codebroker.protocol.serialization.KryoSerialization;
import com.codebroker.setting.SystemEnvironment;
import com.codebroker.util.MathUtil;

import java.time.Duration;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

/**
 * 游戏世界的Actor，代理GameWorld的事件
 */
public class GameWorldWithActor implements IGameWorld {

	private ActorRef<IGameWorldActor> gameWorldActorRef;

	public static EntityTypeKey<IServiceActor> getTypeKey(String name){
		return EntityTypeKey.create(IServiceActor.class,name);
	}

	private String gameWorldId;
	private int shardId= 100;

	public GameWorldWithActor(String gameWorldId, ActorRef<IGameWorldActor> gameWorldActorRef ) {
		this.gameWorldActorRef = gameWorldActorRef;
		this.gameWorldId = gameWorldId;
		if (ActorPathService.akkaConfig.hasPath("akka.cluster.sharding.number-of-shards")){
			this.shardId=ActorPathService.akkaConfig.getInt("akka.cluster.sharding.number-of-shards");
		}
	}

	@Override
	public Optional<IGameUser> findIGameUserById(String id)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  {
		Scheduler scheduler = ContextResolver.getActorSystem().scheduler();
		CompletionStage<IGameWorldActor.Reply> result =
				AskPattern.ask(
						gameWorldActorRef,
						replyTo -> new IGameWorldActor.findIGameUserByIdActor(id, replyTo),
						Duration.ofMillis(SystemEnvironment.TIME_OUT_MILLIS),
						scheduler);
		CompletionStage<IGameUser> handle = result.handle((reply, throwable) -> {
			if (reply instanceof IGameWorldActor.FindGameUser){
				return ((IGameWorldActor.FindGameUser) reply).gameUser;
			}else{
				return null;
			}
		});
		return Optional.ofNullable(handle.toCompletableFuture().join());
	}

	@Override
	public boolean createService(String serviceName, IService  service) {
		ActorSystem<IGameRootSystemMessage> actorSystem = ContextResolver.getActorSystem();

		CompletionStage<IGameRootSystemMessage.Reply> ask = AskPattern.ask(actorSystem,
				replyActorRef -> new IGameRootSystemMessage.createGlobalService(serviceName, service, replyActorRef),
				Duration.ofMillis(SystemEnvironment.TIME_OUT_MILLIS),
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
		return reply != null;
	}

	@Override
	public boolean createService(IService  service) {
		return createService(service.getName(),service);
	}

	@Override
	public boolean createClusterService(String serviceName, IService  service) {
		ActorSystem<IGameRootSystemMessage> actorSystem = ContextResolver.getActorSystem();
		//创建独立的节点
		ClusterSharding clusterSharding = ClusterSharding.get(actorSystem);
		String dataCenter="";
		if (ActorPathService.akkaConfig.hasPath("akka.cluster.multi-data-center.self-data-center")){
			dataCenter = ActorPathService.akkaConfig.getString("akka.cluster.multi-data-center.self-data-center");
		}

		EntityTypeKey<IServiceActor> typeKey = getTypeKey(service.getClass().getName());
		ActorRef<ShardingEnvelope<IServiceActor>> shardRegion =
				clusterSharding.init(Entity.of(
						typeKey,
						ctx -> {
							String ctxEntityId = ctx.getEntityId();
							Behavior<IServiceActor> commandBehavior =
									ClusterServiceActor.create(ctxEntityId, service);
							return commandBehavior;
						}).withDataCenter(dataCenter)
						//停止的时候发的协议
						.withStopMessage(new IServiceActor.Destroy(null))
				);

		ShardingEnvelope<IServiceActor> shardingEnvelope =
				new ShardingEnvelope<>(typeKey.name(), new IServiceActor.Init(""));
		shardRegion.tell(shardingEnvelope);
		ClusterServiceWithActor serviceActor = new ClusterServiceWithActor(typeKey.name(), clusterSharding);
		new ObjectActorDecorate<>(serviceActor, service).newProxyInstance(service.getClass());

		Optional<ZookeeperComponent> component = ContextResolver.getComponent(ZookeeperComponent.class);
		if (component.isPresent()){
			component.get().getIClusterServiceRegister().registerService(serviceName, gameWorldId,ServerEngine.akkaHttpHost,ServerEngine.akkaHttpPort);
		}

		ContextResolver.setManager(service);

		return true;
	}

	@Override
	public boolean createClusterService(IService  service) {
		return createClusterService(service.getName(),service);
	}

	@Override
	public IResultStatusMessage sendMessageToLocalIService(String serviceName, IPacket object){
		if (ActorPathService.localService.containsKey(serviceName)) {
			ActorSystem<IGameRootSystemMessage> actorSystem = ContextResolver.getActorSystem();

			CompletionStage<IServiceActor.Reply> ask = AskPattern.askWithStatus(
					ActorPathService.localService.get(serviceName),
					replyActorRef -> new IServiceActor.HandleUserMessage(object, replyActorRef),
					Duration.ofMillis(SystemEnvironment.TIME_OUT_MILLIS),
					actorSystem.scheduler());

			ask.exceptionally(throwable -> {
				throwable.printStackTrace();
				return null;
			});
			IServiceActor.Reply join = ask.toCompletableFuture().join();
			if (join instanceof IServiceActor.HandleUserMessageBack){
				IServiceActor.HandleUserMessageBack result= (IServiceActor.HandleUserMessageBack) join;
				return ResultStatusMessage.OK(result);
			}else {
				return ResultStatusMessage.FAIL();
			}
		}
		else{
			return ResultStatusMessage.ERROR("No Service "+serviceName);
		}
	}

	@Override
	public IResultStatusMessage sendMessageToClusterIService(Class iService, IPacket message) {
		return sendMessageToClusterIService(iService.getName(),message);
	}

	@Override
	public IResultStatusMessage sendMessageToClusterIService(String serviceName, IPacket message) {
		ActorSystem<IGameRootSystemMessage> actorSystem = ContextResolver.getActorSystem();
		Optional<ZookeeperComponent> component = ContextResolver.getComponent(ZookeeperComponent.class);
		Optional<Collection<String>> cacheService=Optional.empty();
		if (component.isPresent()){
			cacheService = component.get().getIClusterServiceRegister().getCacheService(serviceName);
		}

		Http http = Http.get(actorSystem);
//		String json = message.toJson();
		String json = KryoSerialization.writeObjectToString(message);

		HTTPRequest httpRequest=new HTTPRequest(serviceName,json);
		//节点数量
		String stationUrl;

		byte[] bytes = KryoSerialization.writeObjectToByteArray(httpRequest);

		if (cacheService.isPresent()){
			Collection<String> strings = cacheService.get();
			int randomShardId = MathUtil.random(shardId) + 1;
			for (String string : strings) {
				stationUrl = "http://" + string + "/service/" + randomShardId;
				CompletionStage<IResultStatusMessage> futureResponseBody =
						http.singleRequest(
								HttpRequest.POST(stationUrl)
										.withEntity(ContentTypes.APPLICATION_OCTET_STREAM, bytes))
								.thenCompose(
										response ->
										Unmarshaller.entityToString().unmarshal(response.entity(),SystemMaterializer.get(actorSystem).materializer())
										.thenApply(body -> {
														if (response.status().isSuccess()){
															return KryoSerialization.readObjectFromString(body,ResultStatusMessage.class);
														}else {
															return ResultStatusMessage.ERROR("Http");
														}
													}
												)
								);
//								.thenCompose(response ->
//										Unmarshaller.entityToString().unmarshal(response.entity(), SystemMaterializer.get(actorSystem).materializer())
//												.thenApply(body -> {
//													if (response.status().isSuccess())
//														return body;
//													else throw new RuntimeException("Failed to register data: " + body);
//												})
//								);
				IResultStatusMessage result = futureResponseBody.toCompletableFuture().join();
				return  result;
			}
		}
		return ResultStatusMessage.FAIL();
	}

	@Override
	public IResultStatusMessage sendMessageToLocalIService(Class clazz, IPacket object){
		return sendMessageToLocalIService(clazz.getName(),object);
	}

	@Override
	public void sendMessageToIService(String serviceName, IPacket object){
		/**
		 * 如果是当前系统创建则使用当前系统的
		 */
		if (ActorPathService.localService.containsKey(serviceName)){
			ActorRef<IServiceActor> iServiceActorRef = ActorPathService.localService.get(serviceName);
			iServiceActorRef.tell(new IServiceActor.HandleMessage(object));
		}
		else {
			gameWorldActorRef.tell(new IGameWorldActor.SendActorToService(serviceName,object));
		}
	}

	@Override
	public void sendMessageToIService(Class iService, IPacket message) {
		sendMessageToIService(iService.getName(),message);
	}

	@Override
	public void sendAllOnlineUserMessage(int requestId, IPacket message) {
		gameWorldActorRef.tell(new IGameWorldActor.SendAllOnlineUserActor(requestId,message));
	}

	@Override
	public void sendAllOnlineUserIPacket(IPacket message) {
		gameWorldActorRef.tell(new IGameWorldActor.SendAllOnlineUserPacket(message));
	}

	@Override
	public void sendAllOnlineUserIEvent(IEvent message) {
		gameWorldActorRef.tell(new IGameWorldActor.SendAllOnlineUserEvent(message));
	}

	@Override
	public void restart() {
		IServiceActor.Destroy destroy = new IServiceActor.Destroy("");
		ActorPathService.localService.values().forEach(iServiceActorRef -> iServiceActorRef.tell(destroy));
	}
}
