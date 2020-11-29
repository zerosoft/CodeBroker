package com.codebroker.core.actortype;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.receptionist.Receptionist;
import akka.actor.typed.receptionist.ServiceKey;
import com.codebroker.core.actortype.message.IServiceActor;

import java.util.HashSet;
import java.util.Set;
public class ServiceGuardian {

	private final ActorRef<Receptionist.Listing> listingAdapter;
	private Set<ActorRef<IServiceActor>> serviceInstances = new HashSet();

	private final IServiceActor message;
	private final String serviceName;

	public static Behavior<IServiceActor> create(String serviceName, IServiceActor message) {
		return Behaviors.setup(context -> new ServiceGuardian(context, serviceName, message).handle());
	}

	private ServiceGuardian(ActorContext<IServiceActor> context, String serviceName, IServiceActor message) {
		this.message = message;
		this.serviceName=serviceName;
		// 消息转换器，监听支付处理器的变更消息
		this.listingAdapter = context.messageAdapter(Receptionist.Listing.class, IServiceActor.AddProcessorReference::new);
		context.getSystem()
				.receptionist()
				.tell(Receptionist.subscribe(ServiceKey.create(IServiceActor.class, serviceName), listingAdapter));
	}

	private Behavior<IServiceActor> handle() {
		return Behaviors.receive(IServiceActor.class)
				.onMessage(IServiceActor.AddProcessorReference.class, listing ->
				{
					serviceInstances = listing.listing.getAllServiceInstances(ServiceKey.create(IServiceActor.class, serviceName));
					System.out.println("Service name "+serviceName+"size ="+serviceInstances.size());
					for (ActorRef<IServiceActor> serviceInstance : serviceInstances)
					{
						serviceInstance.tell(message);
					}
					return Behaviors.stopped();
				}).build();
	}


}
