package com.codebroker.core.entities;

import akka.actor.ActorRef;
import com.codebroker.api.IWorld;
import com.codebroker.api.NPCControl;
import com.codebroker.core.EventDispatcher;
import com.codebroker.core.local.WorldCreateNPC;

public class World extends EventDispatcher implements IWorld {

    @Override
    public void createNPC(String npcId, NPCControl control) {
        getActorRef().tell(new WorldCreateNPC(npcId, control), ActorRef.noSender());
    }

}
