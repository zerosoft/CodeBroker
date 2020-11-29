package com.codebroker.core.actortype;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.receptionist.Receptionist;
import akka.actor.typed.receptionist.ServiceKey;
import akka.pattern.StatusReply;
import com.codebroker.api.AppContext;
import com.codebroker.core.actortype.message.IServiceActor;

public class ServiceActor extends AbstractBehavior<IServiceActor> {

    private String name;

    private com.codebroker.api.internal.IService  service;

    public static Behavior<IServiceActor> create(String name, com.codebroker.api.internal.IService  service) {
        return create(name,service,false);
    }

    public static Behavior<IServiceActor> create(String name, com.codebroker.api.internal.IService  service, boolean noServerId) {
        return Behaviors.setup(
                context -> {
                    int serverId = AppContext.getServerId();
                    String id = noServerId ? name : name + "." + serverId;
                    context
                            .getSystem()
                            .receptionist()
                            .tell(Receptionist.register(ServiceKey.create(IServiceActor.class, id), context.getSelf()));
                    return new ServiceActor(context,name,service);
                });
    }

    public ServiceActor(ActorContext<IServiceActor> context, String name, com.codebroker.api.internal.IService  service) {
        super(context);
        this.name=name;
        this.service=service;
    }

    @Override
    public Receive<IServiceActor> createReceive() {
        return newReceiveBuilder()
                .onMessage(IServiceActor.Init.class,this::init)
                .onMessage(IServiceActor.Destroy.class,this::destroy)
                .onMessage(IServiceActor.HandleMessage.class,this::handleMessage)
                .onMessage(IServiceActor.HandleUserMessage.class,this::handleUserMessage)
                .build();
    }

    private  Behavior<IServiceActor> handleUserMessage(IServiceActor.HandleUserMessage message) {
        try {
            Object iObject = service.handleBackMessage(message.object);
            message.Reply.tell(StatusReply.success(new IServiceActor.HandleUserMessageBack(iObject)));
        }catch (Exception e){
            message.Reply.tell(StatusReply.error(e));
        }
        return Behaviors.same();
    }

    private  Behavior<IServiceActor> handleMessage(IServiceActor.HandleMessage message) {
        try {
            service.handleMessage(message.object);
        }catch (Exception e){

        }
        return Behaviors.same();
    }

    private  Behavior<IServiceActor> destroy(IServiceActor.Destroy destroy) {
        service.destroy(destroy.object);
        return Behaviors.stopped();
    }

    private Behavior<IServiceActor> init(IServiceActor.Init message) {
        service.init(message.object);
        return Behaviors.same();
    }
}
