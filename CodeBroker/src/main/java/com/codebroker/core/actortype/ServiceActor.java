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
import com.codebroker.core.ContextResolver;
import com.codebroker.core.actortype.message.IService;
import com.codebroker.core.actortype.message.ISessionManager;
import com.codebroker.core.actortype.message.IUser;
import com.codebroker.core.data.IObject;
import com.codebroker.setting.SystemEnvironment;
import com.codebroker.util.PropertiesWrapper;

public class ServiceActor extends AbstractBehavior<IService> {

    private String name;

    private com.codebroker.api.internal.IService service;

    public static Behavior<IService> create(String name, com.codebroker.api.internal.IService service) {
        return create(name,service,false);
    }

    public static Behavior<IService> create(String name, com.codebroker.api.internal.IService service,boolean noServerId) {
        return Behaviors.setup(
                context -> {
                    int serverId = AppContext.getServerId();
                    context
                            .getSystem()
                            .receptionist()
                            .tell(Receptionist.register(ServiceKey.create(IService.class, noServerId?name:name+"."+serverId), context.getSelf()));
                    return new ServiceActor(context,name,service);
                });
    }

    public ServiceActor(ActorContext<IService> context, String name, com.codebroker.api.internal.IService service) {
        super(context);
        this.name=name;
        this.service=service;
    }

    @Override
    public Receive<IService> createReceive() {
        return newReceiveBuilder()
                .onMessage(IService.Init.class,this::init)
                .onMessage(IService.Destroy.class,this::destroy)
                .onMessage(IService.HandleMessage.class,this::handleMessage)
                .onMessage(IService.HandleUserMessage.class,this::handleUserMessage)
                .build();
    }

    private  Behavior<IService> handleUserMessage(IService.HandleUserMessage message) {
        try {
            IObject iObject = service.handleBackMessage(message.object);
            message.Reply.tell(StatusReply.success(new IService.HandleUserMessageBack(iObject)));
        }catch (RuntimeException e){
            message.Reply.tell(StatusReply.error(e));
        }
        return Behaviors.same();
    }

    private  Behavior<IService> handleMessage(IService.HandleMessage message) {
        try {
            service.handleMessage(message.object);
        }catch (RuntimeException e){

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
