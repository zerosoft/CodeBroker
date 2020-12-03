package com.codebroker.net.http;

import akka.NotUsed;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.AskPattern;
import akka.cluster.sharding.typed.javadsl.ClusterSharding;
import akka.cluster.sharding.typed.javadsl.EntityRef;
import akka.cluster.sharding.typed.javadsl.EntityTypeKey;
import akka.http.javadsl.Http;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.model.ws.Message;
import akka.http.javadsl.model.ws.TextMessage;
import akka.http.javadsl.server.Route;
import akka.http.javadsl.settings.ServerSettings;
import akka.http.javadsl.settings.WebSocketSettings;
import akka.http.javadsl.unmarshalling.Unmarshaller;
import akka.japi.JavaPartialFunction;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Source;
import akka.util.ByteString;
import com.codebroker.core.actortype.GameWorldWithActor;
import com.codebroker.core.actortype.message.IServiceActor;
import com.codebroker.extensions.service.RequestKeyMessage;
import com.codebroker.extensions.service.ResultStatusMessage;
import com.codebroker.protocol.serialization.KryoSerialization;
import com.codebroker.setting.SystemEnvironment;
import org.slf4j.Logger;

import java.time.Duration;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicInteger;

import static akka.http.javadsl.server.Directives.*;
import static akka.http.javadsl.server.PathMatchers.longSegment;
import static akka.http.javadsl.server.PathMatchers.segment;

public class HttpServer {
	public static final String SERVICE_NAME = "servicename";
	public static final String SERVICE = "service";
	public static final String WEB_SOCKET = "webSocket";
	public static final String MESSAGE_PARAMETER_NAME = "message";

	private final ActorSystem actorSystem;
//	private final ObjectMapper objectMapper;
//	private Unmarshaller<HttpEntity, HTTPRequest> dataUnmarshaller;

	public static void start(ActorSystem<?> actorSystem,String host,int port) {
		new HttpServer(host,port , actorSystem);
	}

	private HttpServer(String host, int port, ActorSystem actorSystem) {
		this.actorSystem = actorSystem;
		start(host,port);
//		objectMapper = JacksonObjectMapperProvider.get(actorSystem).getOrCreate("jackson-json", Optional.empty());
//		dataUnmarshaller = Jackson.unmarshaller(objectMapper, HTTPRequest.class);
	}

	private  void start(String host, int port) {
		ServerSettings defaultSettings = ServerSettings.create(actorSystem.classicSystem());

		AtomicInteger pingCounter = new AtomicInteger();

		WebSocketSettings customWebsocketSettings = defaultSettings
				.getWebsocketSettings()
				.withPeriodicKeepAliveData(() ->
						ByteString.fromString(String.format("debug-%d", pingCounter.incrementAndGet()))
				);

		ServerSettings customServerSettings = defaultSettings.withWebsocketSettings(customWebsocketSettings);

		Http.get(actorSystem)
				.newServerAt(host, port)
				.withSettings(customServerSettings)
				.bind(route());

		log().info("HTTP Server started on port {}", "" + port);
	}


	private CompletionStage<String> processClusterGet(long shardId,String serviceName,String message) {
		ClusterSharding sharding = ClusterSharding.get(actorSystem);
		EntityTypeKey<IServiceActor> typeKey = GameWorldWithActor.getTypeKey(serviceName);
		EntityRef<IServiceActor> entityRef = sharding.entityRefFor(typeKey, Long.toString(shardId));

		RequestKeyMessage requestKeyMessage = KryoSerialization.readObjectFromString(message, RequestKeyMessage.class);

		CompletionStage<IServiceActor.Reply> result = AskPattern.askWithStatus(
				entityRef,
				replyActorRef ->  new IServiceActor.HandleUserMessage(requestKeyMessage, replyActorRef),
				Duration.ofMillis(SystemEnvironment.TIME_OUT_MILLIS),
				actorSystem.scheduler());

		CompletionStage<String> objectCompletionStage = result.thenApplyAsync(f -> {
			if (f instanceof IServiceActor.HandleUserMessageBack) {
				IServiceActor.HandleUserMessageBack back = (IServiceActor.HandleUserMessageBack) f;
				return back.object.toString();
			} else {
				return "Optional.empty()";
			}
		});
		return objectCompletionStage;
	}


	private CompletionStage<String> processClusterHTTPRequest(long shardId,HTTPRequest date) {
		ClusterSharding sharding = ClusterSharding.get(actorSystem);
		EntityTypeKey<IServiceActor> typeKey = GameWorldWithActor.getTypeKey(date.serviceName);
		EntityRef<IServiceActor> entityRef = sharding.entityRefFor(typeKey, Long.toString(shardId));

		RequestKeyMessage requestKeyMessage = KryoSerialization.readObjectFromString(date.message, RequestKeyMessage.class);

		CompletionStage<IServiceActor.Reply> result = AskPattern.askWithStatus(
				entityRef,
				replyActorRef ->  new IServiceActor.HandleUserMessage(requestKeyMessage, replyActorRef),
				Duration.ofSeconds(5),
				actorSystem.scheduler());

		CompletionStage<String> objectCompletionStage = result.thenApplyAsync(message -> {
			if (message instanceof IServiceActor.HandleUserMessageBack) {
				IServiceActor.HandleUserMessageBack back = (IServiceActor.HandleUserMessageBack) message;
				return KryoSerialization.writeObjectToString(back.object);
			} else {
				return KryoSerialization.writeObjectToString(ResultStatusMessage.FAIL());
			}
		});
		return objectCompletionStage;
	}

	private Route route() {
		return concat(
				path(WEB_SOCKET, () -> handleWebSocketMessages(websocket())),
				path(segment(SERVICE)
								.slash()
								.concat(longSegment()), shardId ->//集群Id
								concat(
										get(() ->
												parameter(SERVICE_NAME, (
														serviceName -> parameter(MESSAGE_PARAMETER_NAME,(message
																  -> completeOKWithFuture(processClusterGet(shardId,serviceName,message),Jackson.marshaller())
																)
															)
														)
												)
											),
										post(() ->
												entity(Unmarshaller
																.entityToByteArray()
																.thenApply(
																	ms->KryoSerialization.readObjectFromByteArray(ms, HTTPRequest.class)),
																	date ->
																			onSuccess(
																					processClusterHTTPRequest(shardId,date),
																					performed ->complete(StatusCodes.ACCEPTED, performed)
																			)
																)
											)
										)
				)
		);
	}




	public static Flow<Message, Message, NotUsed> websocket() {
		return
				Flow.<Message>create()
						.collect(new JavaPartialFunction<Message, Message>() {

							@Override
							public Message apply(Message msg, boolean isCheck) throws Exception {
								if (isCheck) {
									if (msg.isText()) {
										return null;
									} else {
										throw noMatch();
									}
								} else {

									return handleTextMessage(msg.asTextMessage());
								}
							}
						});
	}

	public static TextMessage handleTextMessage(TextMessage msg) {
		if (msg.isStrict()) // optimization that directly creates a simple response...
		{
			return TextMessage.create("Hello " + msg.getStrictText());
		} else // ... this would suffice to handle all text messages in a streaming fashion
		{
			return TextMessage.create(Source.single("Hello ").concat(msg.getStreamedText()));
		}
	}


	private Logger log() {
		return actorSystem.log();
	}

}