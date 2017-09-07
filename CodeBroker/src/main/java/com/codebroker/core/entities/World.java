package com.codebroker.core.entities;

import com.codebroker.api.IWorld;
import com.codebroker.api.NPCControl;
import com.codebroker.core.EventDispatcher;
import com.codebroker.core.local.WorldCreateNPC;

import akka.actor.ActorRef;

public class World extends EventDispatcher implements IWorld{

	@Override
	public void createNPC(NPCControl control) {
		getActorRef().tell(new WorldCreateNPC(control), ActorRef.noSender());
	}

}
