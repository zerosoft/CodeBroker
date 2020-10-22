package com.codebroker.core.actortype;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.receptionist.Receptionist;
import akka.actor.typed.receptionist.ServiceKey;
import akka.pattern.StatusReply;
import com.codebroker.core.actortype.message.IService;
import com.codebroker.core.data.IObject;

/**
 * 集群Service的Actor对象
 */
public class ClusterServiceActor extends AbstractBehavior<IService> {

    private String name;

    private com.codebroker.api.internal.IService service;

    public static Behavior<IService> create(String name, com.codebroker.api.internal.IService service) {
        return Behaviors.setup(
                context ->new ClusterServiceActor(context,name,service));
//                context -> {
//                    context
//                            .getSystem()
//                            .receptionist()
//                            .tell(Receptionist.register(ServiceKey.create(IService.class, name), context.getSelf()));
//                    return new ClusterServiceActor(context,name,service);
//                });
    }

    public ClusterServiceActor(ActorContext<IService> context, String name, com.codebroker.api.internal.IService service) {
        super(context);
        this.name=name;
        this.service=service;
    }

    @Override
    public Receive<IService> createReceive() {
        return newReceiveBuilder()
                .onMessage(IService.Init.class,this::init)
                .onMessage(IService.Destroy.class,this::destroy)
                .onMessage(IService.HandleUserMessage.class,this::handleUserMessage)
                .onMessage(IService.HandleMessage.class,this::handleMessage)
                .build();
    }



    private  Behavior<IService> handleMessage(IService.HandleMessage message) {
        try {
            service.handleMessage(message.object);
        }catch (RuntimeException e){
            getContext().getLog().error(e.getMessage(),e);
        }
        return Behaviors.same();
    }




    private  Behavior<IService> handleUserMessage(IService.HandleUserMessage message) {
        try {
            IObject iObject = service.handleBackMessage(message.object);
            message.Reply.tell(StatusReply.success(new IService.HandleUserMessageBack(iObject)));
        }catch (Exception e){
            message.Reply.tell(StatusReply.error(e));
        }
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
