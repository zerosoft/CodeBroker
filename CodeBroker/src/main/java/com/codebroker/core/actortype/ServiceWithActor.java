package com.codebroker.core.actortype;

import akka.actor.typed.ActorRef;
import com.codebroker.core.actortype.message.IService;
import com.codebroker.core.data.IObject;

/**
 * Seveice 通过 Akka的actor 执行任务
 */
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
    public void handleMessage(IObject obj) {
        actorActorRef.tell(new IService.HandleMessage(obj));
    }

    @Override
    public String getName() {
        return name;
    }

    public ActorRef<IService> getActorActorRef() {
        return actorActorRef;
    }
}
