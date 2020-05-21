package com.codebroker.core.actortype;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.receptionist.Receptionist;
import akka.actor.typed.receptionist.ServiceKey;
import com.codebroker.api.AppContext;
import com.codebroker.core.actortype.message.IGameWorldMessage;
import com.codebroker.core.actortype.message.IService;

import java.util.HashSet;
import java.util.Set;

public class ServiceGuardian {

	private ActorRef<Receptionist.Listing> listingAdapter;
	private Set<ActorRef<IService>> serviceInstances = new HashSet();

	private ActorRef sender;
	private IService message;
	private String serviceName;

	public static Behavior<IService> create(ActorRef sender, String serviceName, IService message) {
		return Behaviors.setup(context -> new ServiceGuardian(context, sender, serviceName, message).handle());
	}

	private ServiceGuardian(ActorContext<IService> context, ActorRef sender,String serviceName, IService message) {
		this.sender = sender;
		this.message = message;
		this.serviceName=serviceName;
		// 消息转换器，监听支付处理器的变更消息
		this.listingAdapter = context.messageAdapter(Receptionist.Listing.class, IService.AddProcessorReference::new);
		context.getSystem().receptionist().tell(Receptionist.subscribe(ServiceKey.create(IService.class, serviceName), listingAdapter));
	}

	private Behavior<IService> handle() {
		return Behaviors.receive(IService.class)
				.onMessage(IService.AddProcessorReference.class, listing -> {
					int serverId = AppContext.getServerId();
					serviceInstances = listing.listing.getServiceInstances(ServiceKey.create(IService.class, serviceName));
					for (ActorRef<IService> serviceInstance : serviceInstances) {
						serviceInstance.tell(message);
					}
					return Behaviors.stopped();
				}).build();
	}


}
