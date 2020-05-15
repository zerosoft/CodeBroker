package com.codebroker.core.actortype;

import akka.actor.typed.ActorRef;
import com.codebroker.api.internal.IService;
import com.codebroker.core.actortype.message.IServiceActor;

public class ServiceWithActor implements IService {

    private ActorRef<IServiceActor> actorActorRef;
    private String name;

    public ServiceWithActor(String name, ActorRef<IServiceActor> actorActorRef ) {
        this.actorActorRef = actorActorRef;
        this.name = name;
    }

    @Override
    public void init(Object obj) {
        actorActorRef.tell(new IServiceActor.Init(obj));
    }

    @Override
    public void destroy(Object obj) {
        actorActorRef.tell(new IServiceActor.Destroy(obj));
    }

    @Override
    public void handleMessage(Object obj) {
        actorActorRef.tell(new IServiceActor.HandleMessage(obj));
    }

    @Override
    public String getName() {
        return name;
    }

}
