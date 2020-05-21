package com.codebroker.core.actortype;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.receptionist.Receptionist;
import akka.actor.typed.receptionist.ServiceKey;
import com.codebroker.api.AppContext;
import com.codebroker.core.ContextResolver;
import com.codebroker.core.actortype.message.IGameWorldMessage;
import com.codebroker.setting.SystemEnvironment;
import com.codebroker.util.PropertiesWrapper;

import java.util.HashSet;
import java.util.Set;

public class GameWorldGuardian {

	private ActorRef<Receptionist.Listing> listingAdapter;
	private Set<ActorRef<IGameWorldMessage>> serviceInstances = new HashSet();

	private ActorRef sender;
	private IGameWorldMessage message;

	public static Behavior<IGameWorldMessage> create(ActorRef sender,  IGameWorldMessage message) {
		return Behaviors.setup(context -> new GameWorldGuardian(context, sender, message).handle());
	}

	private GameWorldGuardian(ActorContext<IGameWorldMessage> context, ActorRef sender, IGameWorldMessage message) {
		this.sender = sender;
		this.message = message;
		// 消息转换器，监听支付处理器的变更消息
		this.listingAdapter = context.messageAdapter(Receptionist.Listing.class, IGameWorldMessage.AddProcessorReference::new);
		int serverId = AppContext.getServerId();
		context.getSystem().receptionist().tell(Receptionist.subscribe(ServiceKey.create(IGameWorldMessage.class, GameWorld.IDENTIFY + "." + serverId), listingAdapter));
	}

	private Behavior<IGameWorldMessage> handle() {
		return Behaviors.receive(IGameWorldMessage.class)
				.onMessage(IGameWorldMessage.AddProcessorReference.class, listing -> {
					int serverId = AppContext.getServerId();
					serviceInstances = listing.listing.getServiceInstances(ServiceKey.create(IGameWorldMessage.class, GameWorld.IDENTIFY + "." + serverId));
					for (ActorRef<IGameWorldMessage> serviceInstance : serviceInstances) {
						serviceInstance.tell(message);
					}
					return Behaviors.stopped();
				}).build();
	}


}
