package com.codebroker.core.actortype;

import akka.actor.typed.ActorRef;
import akka.cluster.ClusterEvent;
import akka.cluster.sharding.typed.ShardingEnvelope;
import com.codebroker.core.actortype.message.IGameWorldMessage;
import com.codebroker.core.actortype.message.IService;
import com.codebroker.core.actortype.message.ISessionManager;
import com.codebroker.core.actortype.message.IUserManager;
import com.codebroker.core.actortype.timer.UserManagerTimer;
import com.google.common.collect.Maps;

import java.util.Map;

public class ActorPathService {

	protected static Map<String, ActorRef<IService>> localService= Maps.newConcurrentMap();
	protected static Map<String,ActorRef<ShardingEnvelope<IService>>> localClusterService= Maps.newConcurrentMap();

	protected static ActorRef<ISessionManager> sessionManager;
	protected static ActorRef<IUserManager> userManager;
	protected static ActorRef<IGameWorldMessage> gameWorldMessageActorRef;
	protected static ActorRef<UserManagerTimer.Command> userManagerTimer;
	protected static ActorRef<ClusterEvent.ClusterDomainEvent> clusterDomainEventActorRef;
}
