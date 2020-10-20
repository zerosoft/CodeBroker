package com.codebroker.core.actortype;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.receptionist.Receptionist;

import akka.actor.typed.receptionist.ServiceKey;
import com.codebroker.api.AppContext;
import com.codebroker.core.ContextResolver;
import com.codebroker.core.actortype.message.ISession;
import com.codebroker.core.actortype.message.IUserManager;
import com.codebroker.setting.SystemEnvironment;
import com.codebroker.util.PropertiesWrapper;

import java.util.HashSet;
import java.util.Set;

public class UserManagerGuardian {

	private ActorRef<Receptionist.Listing> listingAdapter;
	private Set<ActorRef<IUserManager>> serviceInstances = new HashSet();
	private final IUserManager message;
	private final int serverId;

	public static Behavior<IUserManager> create(IUserManager message,int serverId) {
		return Behaviors.setup(context -> new UserManagerGuardian(context, message,serverId).handle());
	}

	private UserManagerGuardian(ActorContext<IUserManager> context, IUserManager message,int serverId) {
		this.message = message;
		// 消息转换器，监听支付处理器的变更消息
		this.listingAdapter = context.messageAdapter(Receptionist.Listing.class, IUserManager.AddProcessorReference::new);
		this.serverId=serverId;
		context.getSystem().receptionist().tell(Receptionist.subscribe(ServiceKey.create(IUserManager.class, UserManager.IDENTIFY + "." + serverId), listingAdapter));
	}

	private Behavior<IUserManager> handle() {
		return Behaviors.receive(IUserManager.class)
				.onMessage(IUserManager.AddProcessorReference.class, listing -> {
					serviceInstances = listing.listing.getServiceInstances(ServiceKey.create(IUserManager.class, UserManager.IDENTIFY + "." + serverId));
					for (ActorRef<IUserManager> serviceInstance : serviceInstances) {
						serviceInstance.tell(message);
					}
					return Behaviors.stopped();
				}).build();
	}


}
