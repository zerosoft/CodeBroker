package com.codebroker.core.actortype;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.codebroker.core.actortype.message.IService;

public class ServiceActor extends AbstractBehavior<IService> {

    private String id;
    private com.codebroker.api.internal.IService service;

    public static Behavior<IService> create(String id, com.codebroker.api.internal.IService service) {
        Behavior<IService> setup = Behaviors.setup(ctx-> new ServiceActor(ctx,id,service));
        return setup;
    }

    public ServiceActor(ActorContext<IService> context, String id, com.codebroker.api.internal.IService service) {
        super(context);
        this.id=id;
        this.service=service;
    }

    @Override
    public Receive<IService> createReceive() {
        return newReceiveBuilder()
                .onMessage(IService.Init.class,this::init)
                .onMessage(IService.Destroy.class,this::destroy)
                .onMessage(IService.HandleMessage.class,this::handleMessage)
                .build();
    }

    private  Behavior<IService> handleMessage(IService.HandleMessage message) {
        service.handleMessage(message.object);
        return Behaviors.same();
    }

    private  Behavior<IService> destroy(IService.Destroy destroy) {
        service.destroy(destroy.object);
        return Behaviors.stopped();
    }

    private Behavior<IService> init(IService.Init message) {
        service.init(message.object);
        return Behaviors.same();
    }
}
