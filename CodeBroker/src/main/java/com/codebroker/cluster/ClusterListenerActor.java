package com.codebroker.cluster;

import akka.actor.Address;
import akka.actor.AddressFromURIString;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.cluster.ClusterEvent;
import akka.cluster.Member;
import akka.cluster.ddata.LWWMap;
import akka.cluster.ddata.typed.javadsl.DistributedData;
import akka.cluster.ddata.typed.javadsl.ReplicatorMessageAdapter;
import akka.cluster.sharding.ClusterSharding;
import akka.cluster.typed.Cluster;
import akka.cluster.typed.JoinSeedNodes;
import akka.cluster.typed.Subscribe;
import com.codebroker.component.service.ZookeeperComponent;
import com.codebroker.core.ContextResolver;
import com.codebroker.core.actortype.ActorPathService;
import org.slf4j.Logger;
import scala.Option;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.StreamSupport;

public class ClusterListenerActor extends AbstractBehavior<ClusterEvent.ClusterDomainEvent> {
	public static final String IDENTIFY = "clusterListener";
	private final Cluster cluster;
	private final Logger log;

	public static Behavior<ClusterEvent.ClusterDomainEvent> create() {
		return Behaviors.setup(ClusterListenerActor::new);
	}

	private ClusterListenerActor(ActorContext<ClusterEvent.ClusterDomainEvent> context) {
		super(context);

		this.cluster = Cluster.get(context.getSystem());
		this.log = context.getLog();

		subscribeToClusterEvents();
	}

	private void subscribeToClusterEvents() {
		Cluster.get(getContext()
				.getSystem())
				.subscriptions()
				.tell(Subscribe.create(getContext().getSelf(), ClusterEvent.ClusterDomainEvent.class));
	}

	@Override
	public Receive<ClusterEvent.ClusterDomainEvent> createReceive() {
		return newReceiveBuilder()
				.onMessage(ClusterEvent.MemberUp.class,this::memberUp)
				.onMessage(ClusterEvent.MemberExited.class,this::memberExited)
				.onMessage(ClusterEvent.UnreachableMember.class,this::unreachableMember)
				.onMessage(ClusterEvent.ReachableMember.class,this::reachableMember)
				.onMessage(ServerOnline.class,this::serverOnline)
				.onAnyMessage(this::logClusterEvent)
				.build();
	}

	private Behavior<ClusterEvent.ClusterDomainEvent> serverOnline(ServerOnline message) {
		String dataCenter = cluster.selfMember().dataCenter();
		Address address = cluster.selfMember().uniqueAddress().address();

		String host = address.getHost().get();
		Integer port = address.getPort().get();
		Set<String> roles = cluster.selfMember().getRoles();
		if (message.host.equals(host)&&port==message.port){
			return Behaviors.same();
		}else{
			if (message.dataCenter.equals(dataCenter)){
				if (message.roles.containsAll(roles)){
					Collection<Member> values = ActorPathService.clusterService.values();
					for (Member value : values) {
						 address = value.uniqueAddress().address();
						 host =address.getHost().get();
						 port =address.getPort().get();
						 if (message.host.equals(host)&&port==message.port){
						 	return Behaviors.same();
						 }
					}
					log.info("add new member host {} port {} dc {}",message.host,message.host,message.dataCenter);
					List<Address> seedNodes = new ArrayList<>();
					seedNodes.add(AddressFromURIString.parse("akka://CodeBroker@"+host+":"+port));
					Cluster.get(getContext().getSystem()).manager().tell(new JoinSeedNodes(seedNodes));
				}
			}
		}
		return Behaviors.same();
	}

	private Behavior<ClusterEvent.ClusterDomainEvent> reachableMember(ClusterEvent.ReachableMember message) {
		addMember(message.member());
		return Behaviors.same();
	}

	private Behavior<ClusterEvent.ClusterDomainEvent> unreachableMember(ClusterEvent.UnreachableMember message) {
		delMember(message.member());
		return Behaviors.same();
	}


	private Behavior<ClusterEvent.ClusterDomainEvent> memberExited(ClusterEvent.MemberExited message) {
		delMember(message.member());
		return Behaviors.same();
	}

	private Behavior<ClusterEvent.ClusterDomainEvent> memberUp(ClusterEvent.MemberUp message) {
		addMember(message.member());
		return Behaviors.same();
	}

	private void addMember(Member member) {
		Set<String> roles = member.getRoles();
		//akka://CodeBroker@127.0.0.1:2552
		log.info(member.uniqueAddress().address().toString());
			ZookeeperComponent manager = ContextResolver.getComponent(ZookeeperComponent.class);
			manager.getIClusterServiceRegister().registerServer(member.uniqueAddress().longUid(),
					member.address().getHost().get(),
					member.address().getPort().get(),
					member.dataCenter(),
					roles);
			log.info("add new Member - {} host {} port {}", member.uniqueAddress().toString(), member.address().host().get(),member.address().port().get());
			ActorPathService.clusterService.put(member.uniqueAddress().toString(),member);
	}

	private void delMember(Member member) {
		ActorPathService.clusterService.remove(member.uniqueAddress().toString());
	}

	private Behavior<ClusterEvent.ClusterDomainEvent> logClusterEvent(Object clusterEventMessage) {
		log.info("{} - {} sent to {}", getClass().getSimpleName(), clusterEventMessage, cluster.selfMember());
		logClusterMembers();
		return Behaviors.same();
	}

	private void logClusterMembers() {
		logClusterMembers(cluster.state());
	}

	private void logClusterMembers(ClusterEvent.CurrentClusterState currentClusterState) {
		final Optional<Member> old =
				StreamSupport
						.stream(currentClusterState.getMembers().spliterator(), false)
						.reduce((older, member) -> older.isOlderThan(member) ? older : member);

		final Member oldest = old.orElse(cluster.selfMember());
		final Set<Member> unreachable = currentClusterState.getUnreachable();
		final String className = getClass().getSimpleName();

		StreamSupport.stream(currentClusterState.getMembers().spliterator(), false)
				.forEach(new Consumer<Member>() {
					int m = 0;

					@Override
					public void accept(Member member) {
						log.info("{} - {} {}{}{}{}", className, ++m, leader(member), oldest(member), unreachable(member), member);
					}

					private String leader(Member member) {
						return member.address().equals(currentClusterState.getLeader()) ? "(LEADER) " : "";
					}

					private String oldest(Member member) {
						return oldest.equals(member) ? "(OLDEST) " : "";
					}

					private String unreachable(Member member) {
						return unreachable.contains(member) ? "(UNREACHABLE) " : "";
					}
				});

		currentClusterState.getUnreachable()
				.forEach(new Consumer<Member>() {
					int m = 0;

					@Override
					public void accept(Member member) {
						log.info("{} - {} {} (unreachable)", getClass().getSimpleName(), ++m, member);
					}
				});
	}
}

