package com.codebroker.core.actortype;

import akka.actor.typed.*;
import akka.actor.typed.javadsl.*;
import akka.cluster.Member;
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
import com.codebroker.api.internal.IService;
import com.codebroker.core.ContextResolver;
import com.codebroker.core.actortype.message.IGameWorldMessage;
import com.codebroker.core.actortype.message.IGameRootSystemMessage;
import com.codebroker.core.data.CObject;
import com.codebroker.core.data.IObject;
import com.codebroker.net.http.HTTPRequest;
import com.google.gson.Gson;

import java.time.Duration;
import java.util.Collection;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletionStage;

/**
 * 游戏世界的Actor，代理GameWorld的事件
 */
public class GameWorldWithActor implements IGameWorld {

	public static final int TIME_OUT_MILLIS = 5000;
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
	public Optional<IGameUser> findIGameUserById(String id)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  {
		Scheduler scheduler = ContextResolver.getActorSystem().scheduler();
		CompletionStage<IGameWorldMessage.Reply> result =
				AskPattern.ask(
						gameWorldActorRef,
						replyTo -> new IGameWorldMessage.findIGameUserByIdMessage(id, replyTo),
						Duration.ofMillis(TIME_OUT_MILLIS),
						scheduler);
		CompletionStage<IGameUser> handle = result.handle((reply, throwable) -> {
			if (reply instanceof IGameWorldMessage.FindGameUser){
				return ((IGameWorldMessage.FindGameUser) reply).gameUser;
			}else{
				return null;
			}
		});
		return Optional.ofNullable(handle.toCompletableFuture().join());
	}

	@Override
	public boolean createService(String serviceName, IService service) {
		ActorSystem<IGameRootSystemMessage> actorSystem = ContextResolver.getActorSystem();

		CompletionStage<IGameRootSystemMessage.Reply> ask = AskPattern.ask(actorSystem,
				replyActorRef -> new IGameRootSystemMessage.createGlobalService(serviceName, service, replyActorRef),
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
		return reply != null;
	}

	@Override
	public boolean createService(IService service) {
		return createService(service.getName(),service);
	}

	@Override
	public boolean createClusterService(String serviceName, IService service) {
		ActorSystem<IGameRootSystemMessage> actorSystem = ContextResolver.getActorSystem();
		//创建独立的节点
//		if (!annotation.sharding()){
//			ClusterSingleton singleton = ClusterSingleton.get(actorSystem);
//
//			ActorRef<com.codebroker.core.actortype.message.IService> serviceActorRef =
//					annotation.dateCenter()==""?
//							singleton.init(SingletonActor.of(ServiceActor.create(serviceName, service), serviceName)):
//							singleton.init(SingletonActor.of(ServiceActor.create(serviceName, service), serviceName)
//									.withSettings(ClusterSingletonSettings.create(actorSystem).withDataCenter(annotation.dateCenter()))
//							);
//
//			ServiceWithActor serviceActor=new ServiceWithActor(serviceName,serviceActorRef);
//
//			new ObjectActorDecorate<>(serviceActor, service).newProxyInstance(service.getClass());
//
//			ActorPathService.localService.put(serviceName,serviceActorRef);
//
//			ContextResolver.setManager(service);
//			return true;
//		}else{
		ClusterSharding clusterSharding = ClusterSharding.get(actorSystem);


		EntityTypeKey<com.codebroker.core.actortype.message.IService> typeKey = getTypeKey(service.getClass().getName());
		ActorRef<ShardingEnvelope<com.codebroker.core.actortype.message.IService>> shardRegion =
				clusterSharding.init(Entity.of(
						typeKey,
						ctx -> {
							String ctxEntityId = ctx.getEntityId();
							Behavior<com.codebroker.core.actortype.message.IService> commandBehavior =
									ClusterServiceActor.create(ctxEntityId, service);
							return commandBehavior;
						})
						//停止的时候发的协议
						.withStopMessage(new com.codebroker.core.actortype.message.IService.Destroy(null))
				);

		ShardingEnvelope<com.codebroker.core.actortype.message.IService> shardingEnvelope =
				new ShardingEnvelope<>(typeKey.name(), new com.codebroker.core.actortype.message.IService.Init(CObject.newInstance()));
		shardRegion.tell(shardingEnvelope);
		ClusterServiceWithActor serviceActor = new ClusterServiceWithActor(typeKey.name(), clusterSharding);
		new ObjectActorDecorate<>(serviceActor, service).newProxyInstance(service.getClass());

		ContextResolver.setManager(service);

		return true;
	}

	@Override
	public boolean createClusterService(IService service) {
		return createService(service.getName(),service);
	}

	@Override
	public Optional<IObject> sendMessageToLocalIService(String serviceName, IObject object){
		if (ActorPathService.localService.containsKey(serviceName)) {
			ActorSystem<IGameRootSystemMessage> actorSystem = ContextResolver.getActorSystem();

			CompletionStage<com.codebroker.core.actortype.message.IService.Reply> ask = AskPattern.askWithStatus(
					ActorPathService.localService.get(serviceName),
					replyActorRef -> new com.codebroker.core.actortype.message.IService.HandleUserMessage(object, replyActorRef),
					Duration.ofMillis(TIME_OUT_MILLIS),
					actorSystem.scheduler());

			ask.exceptionally(throwable -> {
				throwable.printStackTrace();
				return null;
			});
			com.codebroker.core.actortype.message.IService.Reply join = ask.toCompletableFuture().join();
			if (join instanceof com.codebroker.core.actortype.message.IService.HandleUserMessageBack){
				com.codebroker.core.actortype.message.IService.HandleUserMessageBack result= (com.codebroker.core.actortype.message.IService.HandleUserMessageBack) join;
				return Optional.of(result.object);
			}else {
				return Optional.empty();
			}
		}
		else{
			return Optional.empty();
		}
	}

	@Override
	public Optional<IObject> sendMessageToClusterIService(Class iService, IObject message) {
		return Optional.empty();
	}

	@Override
	public Optional<IObject> sendMessageToClusterIService(String serviceName, IObject message) {
		ActorSystem<IGameRootSystemMessage> actorSystem = ContextResolver.getActorSystem();
		Http http = Http.get(actorSystem);
		String json = message.toJson();
		HTTPRequest httpRequest=new HTTPRequest(serviceName,json);
		Collection<Member> values = ActorPathService.clusterService.values();
		Optional<Member> first = values.stream().findFirst();
		String stationUrl;
		Gson gson=new Gson();
		String toJson = gson.toJson(httpRequest, HTTPRequest.class);
		if (first.isPresent()){
			Member member = first.get();
			Random random=new Random();
			int shardId = ActorPathService.akkaConfig.getInt("akka.cluster.sharding.number-of-shards");
			stationUrl = "http://" + member.address().getHost().get()
					+ ":" + (member.address().getPort().get()+7000) + "/service/" + (random.nextInt(shardId)+1);
			CompletionStage<String> futureResponseBody =
					http.singleRequest(
							HttpRequest.POST(stationUrl)
									.withEntity(ContentTypes.APPLICATION_JSON, toJson))
							.thenCompose(response ->
									Unmarshaller.entityToString().unmarshal(response.entity(), SystemMaterializer.get(actorSystem).materializer())
											.thenApply(body -> {
												if (response.status().isSuccess())
													return body;
												else throw new RuntimeException("Failed to register data: " + body);
											})
							);
			String join = futureResponseBody.toCompletableFuture().join();
			return Optional.of(CObject.newFromJsonData(join));
		}

		return Optional.empty();
	}

	@Override
	public Optional<IObject> sendMessageToLocalIService(Class clazz, IObject object){
		return sendMessageToLocalIService(clazz.getSimpleName(),object);
	}

	@Override
	public void sendMessageToIService(String serviceName, IObject object){
		/**
		 * 如果是当前系统创建则使用当前系统的
		 */
		if (ActorPathService.localService.containsKey(serviceName)){
			ActorRef<com.codebroker.core.actortype.message.IService> iServiceActorRef = ActorPathService.localService.get(serviceName);
			iServiceActorRef.tell(new com.codebroker.core.actortype.message.IService.HandleMessage(object));
		}
		else {
			gameWorldActorRef.tell(new IGameWorldMessage.SendMessageToService(serviceName,object));
		}
	}

	@Override
	public void sendMessageToIService(Class iService, IObject message) {
		sendMessageToIService(iService.getSimpleName(),message);
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
	}
}
