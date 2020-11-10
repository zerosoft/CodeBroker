package com.codebroker.core.actortype;

import akka.actor.typed.ActorRef;
import akka.cluster.ClusterEvent;
import akka.cluster.Member;
import akka.cluster.sharding.typed.ShardingEnvelope;
import com.codebroker.core.actortype.message.IGameWorldMessage;
import com.codebroker.core.actortype.message.IService;
import com.codebroker.core.actortype.message.ISessionManager;
import com.codebroker.core.actortype.message.IUserManager;
import com.codebroker.core.actortype.timer.UserManagerTimer;
import com.google.common.collect.Maps;
import com.typesafe.config.Config;

import java.util.Map;

/**
 * 管理也存在系统的Actor地址服务
 */
public class ActorPathService{

	/**
	 * 本节点存在的IService的地址
	 */
	protected static Map<String, ActorRef<IService>> localService= Maps.newConcurrentMap();
	/**
	 * 本节点存在的集群Actor的地址
	 */
	public static Map<String, Member> clusterService = Maps.newConcurrentMap();

	public static Config akkaConfig;
	//网络会话管理
	protected static ActorRef<ISessionManager> sessionManager;
	//用户管理
	protected static ActorRef<IUserManager> userManager;
	protected static ActorRef<IGameWorldMessage> gameWorldMessageActorRef;
	protected static ActorRef<ClusterEvent.ClusterDomainEvent> clusterDomainEventActorRef;
}
