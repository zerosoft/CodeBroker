package com.codebroker.net.http;

import akka.NotUsed;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.AskPattern;
import akka.cluster.ClusterEvent;
import akka.cluster.Member;
import akka.cluster.MemberStatus;
import akka.cluster.sharding.typed.javadsl.ClusterSharding;
import akka.cluster.sharding.typed.javadsl.EntityRef;
import akka.cluster.sharding.typed.javadsl.EntityTypeKey;
import akka.cluster.typed.Cluster;
import akka.http.javadsl.Http;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.ContentTypes;
import akka.http.javadsl.model.HttpEntity;
import akka.http.javadsl.model.MediaTypes;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.model.headers.RawHeader;
import akka.http.javadsl.model.ws.Message;
import akka.http.javadsl.model.ws.TextMessage;
import akka.http.javadsl.server.Route;
import akka.http.javadsl.settings.ServerSettings;
import akka.http.javadsl.settings.WebSocketSettings;
import akka.http.javadsl.unmarshalling.Unmarshaller;
import akka.japi.JavaPartialFunction;
import akka.serialization.jackson.JacksonObjectMapperProvider;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Source;
import akka.util.ByteString;
import com.codebroker.core.actortype.GameWorldWithActor;
import com.codebroker.core.actortype.message.IServiceActor;
import com.codebroker.core.data.CObject;
import com.codebroker.core.data.IObject;
import com.codebroker.protocol.serialization.KryoSerialization;
import com.codebroker.setting.SystemEnvironment;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.slf4j.Logger;
import scala.Option;

import java.io.File;
import java.io.Serializable;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static akka.http.javadsl.server.Directives.*;
import static akka.http.javadsl.server.PathMatchers.longSegment;
import static akka.http.javadsl.server.PathMatchers.segment;

public class HttpServer {
	public static final String RESOURCE = "D:\\Users\\Documents\\github\\CodeBrokerGit\\CodeBroker\\src\\main\\resource\\";
	private final ActorSystem actorSystem;
	private final ObjectMapper objectMapper;
	private Unmarshaller<HttpEntity, HTTPRequest> dataUnmarshaller;

	public static void start(ActorSystem<?> actorSystem,String host,int port) {
		new HttpServer(host,port , actorSystem);
	}

	private HttpServer(String host, int port, ActorSystem actorSystem) {
		this.actorSystem = actorSystem;
		start(host,port);
		objectMapper = JacksonObjectMapperProvider.get(actorSystem).getOrCreate("jackson-json", Optional.empty());
		dataUnmarshaller = Jackson.unmarshaller(objectMapper, HTTPRequest.class);
	}

	private void start(String host, int port) {
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

		IObject iObject = CObject.newFromJsonData(message);

		CompletionStage<IServiceActor.Reply> result = AskPattern.askWithStatus(
				entityRef,
				replyActorRef ->  new IServiceActor.HandleUserMessage(iObject, replyActorRef),
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

		IObject iObject = CObject.newFromJsonData(date.message);

		CompletionStage<IServiceActor.Reply> result = AskPattern.askWithStatus(
				entityRef,
				replyActorRef ->  new IServiceActor.HandleUserMessage(iObject, replyActorRef),
				Duration.ofSeconds(5),
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

	private Route route() {
		return concat(
				path("", () -> getFromFile(new File(RESOURCE + "dashboard.html"), ContentTypes.TEXT_HTML_UTF8)),
				path("dashboard.html", () -> getFromFile(new File(RESOURCE + "dashboard.html"), ContentTypes.TEXT_HTML_UTF8)),
				path("dashboard.js", () -> getFromFile(new File(RESOURCE + "dashboard.js"), ContentTypes.APPLICATION_JSON)),
				path("p5.js", () -> getFromFile(new File(RESOURCE + "p5.js"), ContentTypes.APPLICATION_JSON)),
				path("favicon.ico", () -> getFromFile(new File(RESOURCE + "favicon.ico"), MediaTypes.IMAGE_X_ICON.toContentType())),
				path("cluster-state", this::clusterState),
				path("webSocket", () -> handleWebSocketMessages(websocket())),
				path(segment("service")
								.slash()
								.concat(longSegment()), shardId ->//集群Id
								concat(
										get(() ->
												parameter("servicename", (
														serviceName -> parameter("message",(message
																  -> completeOKWithFuture(processClusterGet(shardId,serviceName,message),Jackson.marshaller())
																)
															)
														)
												)
										),
										post(() -> entity(Unmarshaller.entityToByteArray().thenApply(
												ms->
														KryoSerialization.readObjectFromByteArray(ms, HTTPRequest.class)),
												date ->
														onSuccess(processClusterHTTPRequest(shardId,date), performed ->
																complete(StatusCodes.ACCEPTED, performed)
														)
												)
										)

				))
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

//  public static HttpResponse handleRequest(HttpRequest request) {
//
//    if (request.getUri().path().equals("/webSocket")) {
//      return request
//              .getAttribute(AttributeKeys.webSocketUpgrade())
//              .map(upgrade -> {
//                Flow<Message, Message, NotUsed> greeterFlow = websocket();
//
//                HttpResponse response = upgrade.handleMessagesWith(greeterFlow);
//                return response;
//              })
//              .orElse(
//                      HttpResponse.create().withStatus(StatusCodes.BAD_REQUEST).withEntity("Expected WebSocket request")
//              );
//    } else {
//       return HttpResponse.create().withStatus(404);
//    }
//  }

	private Route clusterState() {
		return get(
				() -> respondWithHeader(RawHeader.create("Access-Control-Allow-Origin", "*"),
						() -> complete(loadNodes(actorSystem).toJson()))
		);
	}

	private static Nodes loadNodes(ActorSystem<?> actorSystem) {
		final Cluster cluster = Cluster.get(actorSystem);
		final ClusterEvent.CurrentClusterState clusterState = cluster.state();

		final Set<Member> unreachable = clusterState.getUnreachable();

		final Optional<Member> old = StreamSupport.stream(clusterState.getMembers().spliterator(), false)
				.filter(member -> member.status().equals(MemberStatus.up()))
				.filter(member -> !(unreachable.contains(member)))
				.reduce((older, member) -> older.isOlderThan(member) ? older : member);

		final Member oldest = old.orElse(cluster.selfMember());

		final List<Integer> seedNodePorts = seedNodePorts(actorSystem);

		final Nodes nodes = new Nodes(
				memberPort(cluster.selfMember()),
				cluster.selfMember().address().equals(clusterState.getLeader()),
				oldest.equals(cluster.selfMember()));

		StreamSupport.stream(clusterState.getMembers().spliterator(), false)
				.forEach(new Consumer<Member>() {
					@Override
					public void accept(Member member) {
						nodes.add(member, leader(member), oldest(member), seedNode(member));
					}

					private boolean leader(Member member) {
						return member.address().equals(clusterState.getLeader());
					}

					private boolean oldest(Member member) {
						return oldest.equals(member);
					}

					private boolean seedNode(Member member) {
						return seedNodePorts.contains(memberPort(member));
					}
				});

		clusterState.getUnreachable()
				.forEach(nodes::addUnreachable);

		return nodes;
	}

	private Logger log() {
		return actorSystem.log();
	}

	private static boolean isValidPort(int port) {
		return port >= 2551 && port <= 2559;
	}

	private static int memberPort(Member member) {
		final Option<Object> portOption = member.address().port();
		return portOption.isDefined()
				? Integer.parseInt(portOption.get().toString())
				: 0;
	}

	private static List<Integer> seedNodePorts(ActorSystem<?> actorSystem) {
		return actorSystem.settings().config().getList("akka.cluster.seed-nodes")
				.stream().map(s -> s.unwrapped().toString())
				.map(s -> {
					final String[] split = s.split(":");
					return split.length == 0 ? 0 : Integer.parseInt(split[split.length - 1]);
				}).collect(Collectors.toList());
	}

	public static class Nodes implements Serializable {
		public final int selfPort;
		public final boolean leader;
		public final boolean oldest;
		public List<Node> nodes = new ArrayList<>();

		public Nodes(int selfPort, boolean leader, boolean oldest) {
			this.selfPort = selfPort;
			this.leader = leader;
			this.oldest = oldest;
		}

		void add(Member member, boolean leader, boolean oldest, boolean seedNode) {
			final int port = memberPort(member);
			if (isValidPort(port)) {
				nodes.add(new Node(port, state(member.status()), memberStatus(member.status()), leader, oldest, seedNode));
			}
		}

		void addUnreachable(Member member) {
			final int port = memberPort(member);
			if (isValidPort(port)) {
				Node node = new Node(port, "unreachable", "unreachable", false, false, false);
				nodes.remove(node);
				nodes.add(node);
			}
		}

		private static String state(MemberStatus memberStatus) {
			if (memberStatus.equals(MemberStatus.down())) {
				return "down";
			} else if (memberStatus.equals(MemberStatus.joining())) {
				return "starting";
			} else if (memberStatus.equals(MemberStatus.weaklyUp())) {
				return "starting";
			} else if (memberStatus.equals(MemberStatus.up())) {
				return "up";
			} else if (memberStatus.equals(MemberStatus.exiting())) {
				return "stopping";
			} else if (memberStatus.equals(MemberStatus.leaving())) {
				return "stopping";
			} else if (memberStatus.equals(MemberStatus.removed())) {
				return "stopping";
			} else {
				return "offline";
			}
		}

		private static String memberStatus(MemberStatus memberStatus) {
			if (memberStatus.equals(MemberStatus.down())) {
				return "down";
			} else if (memberStatus.equals(MemberStatus.joining())) {
				return "joining";
			} else if (memberStatus.equals(MemberStatus.weaklyUp())) {
				return "weaklyup";
			} else if (memberStatus.equals(MemberStatus.up())) {
				return "up";
			} else if (memberStatus.equals(MemberStatus.exiting())) {
				return "exiting";
			} else if (memberStatus.equals(MemberStatus.leaving())) {
				return "leaving";
			} else if (memberStatus.equals(MemberStatus.removed())) {
				return "removed";
			} else {
				return "unknown";
			}
		}

		String toJson() {
			final ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			try {
				return ow.writeValueAsString(this);
			} catch (JsonProcessingException e) {
				return String.format("{ \"error\" : \"%s\" }", e.getMessage());
			}
		}
	}

	public static class Node implements Serializable {
		public final int port;
		public final String state;
		public final String memberState;
		public final boolean leader;
		public final boolean oldest;
		public final boolean seedNode;

		public Node(int port, String state, String memberState, boolean leader, boolean oldest, boolean seedNode) {
			this.port = port;
			this.state = state;
			this.memberState = memberState;
			this.leader = leader;
			this.oldest = oldest;
			this.seedNode = seedNode;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Node node = (Node) o;
			return Objects.equals(port, node.port);
		}

		@Override
		public int hashCode() {
			return Objects.hash(port);
		}
	}
}