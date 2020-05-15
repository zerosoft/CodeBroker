package com.codebroker.core.actortype;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.codebroker.api.internal.IService;
import com.codebroker.core.actortype.message.IServiceActor;

public class ServiceActor extends AbstractBehavior<IServiceActor> {

    private String id;
    private IService service;

    public static Behavior<IServiceActor> create(String id,IService service) {
        Behavior<IServiceActor> setup = Behaviors.setup(ctx-> new ServiceActor(ctx,id,service));
        return setup;
    }

    public ServiceActor(ActorContext<IServiceActor> context, String id, IService service) {
        super(context);
        this.id=id;
        this.service=service;
    }

    @Override
    public Receive<IServiceActor> createReceive() {
        return newReceiveBuilder()
                .onMessage(IServiceActor.Init.class,this::init)
                .onMessage(IServiceActor.Destroy.class,this::destroy)
                .onMessage(IServiceActor.HandleMessage.class,this::handleMessage)
                .build();
    }

    private  Behavior<IServiceActor> handleMessage(IServiceActor.HandleMessage message) {
        service.handleMessage(message.object);
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
