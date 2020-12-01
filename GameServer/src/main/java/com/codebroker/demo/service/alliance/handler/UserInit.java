package com.codebroker.demo.service.alliance.handler;

import com.codebroker.api.IGameUser;
import com.codebroker.extensions.service.IServiceClientRequestHandler;
import com.codebroker.extensions.service.RequestKeyMessage;
import com.codebroker.protocol.serialization.KryoSerialization;
import com.google.gson.JsonObject;

public class UserInit implements IServiceClientRequestHandler<JsonObject> {
	@Override
	public Object handleBackMessage(JsonObject o) {
		getClientRequestLogger().info("User init {}",o);
		String iGameUser = o.get("IGameUser").getAsString();
		IGameUser iGameUser1 = KryoSerialization.readObjectFromString(iGameUser, IGameUser.class);
		iGameUser1.sendMessageToIoSession(12,"Hello".getBytes());
		return "alliance SomeThing";
	}
}
