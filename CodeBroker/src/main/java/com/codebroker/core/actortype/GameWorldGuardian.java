package com.codebroker.core.actortype;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.receptionist.Receptionist;
import akka.actor.typed.receptionist.ServiceKey;
import com.codebroker.api.AppContext;
import com.codebroker.core.actortype.message.IGameWorldActor;

import java.util.HashSet;
import java.util.Set;

public class GameWorldGuardian {

	private ActorRef<Receptionist.Listing> listingAdapter;
	private Set<ActorRef<IGameWorldActor>> serviceInstances = new HashSet();

	private IGameWorldActor message;

	public static Behavior<IGameWorldActor> create(IGameWorldActor message) {
		return Behaviors.setup(context -> new GameWorldGuardian(context, message).handle());
	}

	private GameWorldGuardian(ActorContext<IGameWorldActor> context, IGameWorldActor message) {
		this.message = message;
		// 消息转换器，监听支付处理器的变更消息
		this.listingAdapter = context.messageAdapter(Receptionist.Listing.class, IGameWorldActor.AddProcessorReference::new);
		int serverId = AppContext.getServerId();
		context.getSystem().receptionist().tell(Receptionist.subscribe(ServiceKey.create(IGameWorldActor.class, GameWorld.IDENTIFY + "." + serverId), listingAdapter));
	}

	private Behavior<IGameWorldActor> handle() {
		return Behaviors.receive(IGameWorldActor.class)
				.onMessage(IGameWorldActor.AddProcessorReference.class, listing -> {
					int serverId = AppContext.getServerId();
					serviceInstances = listing.listing.getServiceInstances(ServiceKey.create(IGameWorldActor.class,
							GameWorld.IDENTIFY + "." + serverId));
					for (ActorRef<IGameWorldActor> serviceInstance : serviceInstances) {
						serviceInstance.tell(message);
					}
					return Behaviors.stopped();
				}).build();
	}

}
