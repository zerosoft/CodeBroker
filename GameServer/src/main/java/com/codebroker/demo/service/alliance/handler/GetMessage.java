package com.codebroker.demo.service.alliance.handler;

import com.codebroker.api.IGameUser;
import com.codebroker.demo.service.alliance.message.GetAllianceName;
import com.codebroker.extensions.service.IServiceClientRequestHandler;
import com.codebroker.protocol.serialization.KryoSerialization;
import com.google.gson.JsonObject;

public class GetMessage implements IServiceClientRequestHandler<GetAllianceName> {
	@Override
	public Object handleBackMessage(GetAllianceName o) {
		getClientRequestLogger().info("GetAllianceName init {}",o);
		IGameUser iGameUser1 =o.gameUser;
		iGameUser1.sendMessageToIoSession(12,"Hello".getBytes());
		return "alliance SomeThing";
	}
}
