package com.codebroker.core.actortype;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.pattern.StatusReply;
import com.codebroker.core.actortype.message.IServiceActor;

/**
 * 集群Service的Actor对象
 */
public class ClusterServiceActor extends AbstractBehavior<IServiceActor> {

    private String name;

    private com.codebroker.api.internal.IService service;

    public static Behavior<IServiceActor> create(String name, com.codebroker.api.internal.IService  service) {
        return Behaviors.setup(
                context ->new ClusterServiceActor(context,name,service));
    }

    public ClusterServiceActor(ActorContext<IServiceActor> context, String name, com.codebroker.api.internal.IService  service) {
        super(context);
        this.name=name;
        this.service=service;
    }

    @Override
    public Receive<IServiceActor> createReceive() {
        return newReceiveBuilder()
                .onMessage(IServiceActor.Init.class,this::init)
                .onMessage(IServiceActor.Destroy.class,this::destroy)
                .onMessage(IServiceActor.HandleUserMessage.class,this::handleUserMessage)
                .onMessage(IServiceActor.HandleMessage.class,this::handleMessage)
                .build();
    }

    private  Behavior<IServiceActor> handleMessage(IServiceActor.HandleMessage message) {
        try {
            service.handleMessage(message.object);
        }catch (RuntimeException e){
            getContext().getLog().error(e.getMessage(),e);
        }
        return Behaviors.same();
    }

    private  Behavior<IServiceActor> handleUserMessage(IServiceActor.HandleUserMessage message) {
        try {
            Object iObject =  service.handleBackMessage(message.object);
            message.Reply.tell(StatusReply.success(new IServiceActor.HandleUserMessageBack(iObject)));
        }catch (Exception e){
            message.Reply.tell(StatusReply.error(e));
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
