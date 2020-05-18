package com.codebroker.core.actortype;

import akka.actor.typed.ActorRef;
import com.codebroker.core.actortype.message.IService;

public class ServiceWithActor implements com.codebroker.api.internal.IService {

    private ActorRef<IService> actorActorRef;
    private String name;

    public ServiceWithActor(String name, ActorRef<IService> actorActorRef ) {
        this.actorActorRef = actorActorRef;
        this.name = name;
    }

    @Override
    public void init(Object obj) {
        actorActorRef.tell(new IService.Init(obj));
    }

    @Override
    public void destroy(Object obj) {
        actorActorRef.tell(new IService.Destroy(obj));
    }

    @Override
    public void handleMessage(Object obj) {
        actorActorRef.tell(new IService.HandleMessage(obj));
    }

    @Override
    public String getName() {
        return name;
    }

}
