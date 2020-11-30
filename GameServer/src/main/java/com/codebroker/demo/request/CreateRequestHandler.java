package com.codebroker.demo.request;

import com.codebroker.api.AppContext;
import com.codebroker.api.IGameUser;
import com.codebroker.demo.AbstractClientRequestHandler;
import com.codebroker.demo.service.alliance.AllianceService;
import com.codebroker.extensions.service.RequestKeyMessage;
import com.codebroker.protobuff.user.CreateRequest;
import com.google.gson.JsonObject;


public class CreateRequestHandler extends AbstractClientRequestHandler<CreateRequest> {
	@Override
	public void handleClientProtocolBuffersRequest(IGameUser iGameUser, CreateRequest request) {
		JsonObject jsonObject=new JsonObject();

		jsonObject.addProperty("hello","world");

		RequestKeyMessage requestKeyMessage=new RequestKeyMessage<Integer, JsonObject>(1,jsonObject);
//		cObject.putClass("IGameUser",iGameUser);
		AppContext.getGameWorld().sendMessageToIService(AllianceService.class,requestKeyMessage);

		AppContext.getGameWorld().sendMessageToLocalIService(AllianceService.class,requestKeyMessage);

		AppContext.getGameWorld().sendMessageToIService(AllianceService.class.getName()+".1",requestKeyMessage);

		AppContext.getGameWorld().sendMessageToIService(AllianceService.class.getName()+".2",requestKeyMessage);

		AppContext.getGameWorld().sendMessageToIService(AllianceService.class.getName()+".5",requestKeyMessage);
	}
}