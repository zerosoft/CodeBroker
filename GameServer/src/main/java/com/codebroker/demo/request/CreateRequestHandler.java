package com.codebroker.demo.request;

import com.codebroker.api.AppContext;
import com.codebroker.api.IGameUser;
import com.codebroker.demo.AbstractClientRequestHandler;
import com.codebroker.demo.service.alliance.AllianceService;
import com.codebroker.demo.service.alliance.message.GetAllianceName;
import com.codebroker.extensions.service.RequestKeyMessage;
import com.codebroker.protobuff.user.CreateRequest;
import com.codebroker.protocol.serialization.KryoSerialization;
import com.google.gson.JsonObject;


public class CreateRequestHandler extends AbstractClientRequestHandler<CreateRequest> {
	@Override
	public void handleClientProtocolBuffersRequest(IGameUser iGameUser, CreateRequest request) {
		JsonObject jsonObject=new JsonObject();

		jsonObject.addProperty("hello","world");
		jsonObject.addProperty("IGameUser", KryoSerialization.writeObjectToString(iGameUser));

		GetAllianceName getAllianceName=new GetAllianceName(iGameUser,"HelloWorld");

		RequestKeyMessage requestKeyMessage=new RequestKeyMessage<Integer, GetAllianceName>(4,getAllianceName);
//		cObject.putClass("IGameUser",iGameUser);
		AppContext.getGameWorld().sendMessageToIService(AllianceService.class,requestKeyMessage);

		AppContext.getGameWorld().sendMessageToLocalIService(AllianceService.class,requestKeyMessage);

		AppContext.getGameWorld().sendMessageToIService(AllianceService.class.getName()+".1",requestKeyMessage);

		AppContext.getGameWorld().sendMessageToIService(AllianceService.class.getName()+".2",requestKeyMessage);

		AppContext.getGameWorld().sendMessageToIService(AllianceService.class.getName()+".5",requestKeyMessage);
	}
}