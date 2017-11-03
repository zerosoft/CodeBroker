package com.codebroker.core.entities;

import akka.actor.ActorRef;
import com.codebroker.api.IWorld;
import com.codebroker.api.NPCControl;
import com.codebroker.core.local.WorldCreateNPC;
import com.codebroker.exception.NoActorRefException;

public class World implements IWorld {
    private ActorRef actorRef;

    public ActorRef getActorRef() {
        if (actorRef == null) {
            throw new NoActorRefException();
        }
        return actorRef;
    }

    public void setActorRef(ActorRef gridRef) {
        this.actorRef = gridRef;
    }
    @Override
    public void createNPC(String npcId, NPCControl control) {
        getActorRef().tell(new WorldCreateNPC(npcId, control), ActorRef.noSender());
    }

}
