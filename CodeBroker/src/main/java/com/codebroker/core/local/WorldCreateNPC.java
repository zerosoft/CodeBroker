package com.codebroker.core.local;

import com.codebroker.api.NPCControl;

public class WorldCreateNPC {
	public final String NPCId;
	public final NPCControl control;
	
	public WorldCreateNPC(String nPCId, NPCControl control) {
		super();
		NPCId = nPCId;
		this.control = control;
	}
	
	
}
