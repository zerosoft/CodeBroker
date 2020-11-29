package com.codebroker.demo.request;

import com.codebroker.api.AppContext;
import com.codebroker.api.IGameUser;
import com.codebroker.core.data.CObject;
import com.codebroker.demo.AbstractClientRequestHandler;
import com.codebroker.demo.service.alliance.AllianceService;
import com.codebroker.protobuff.user.CreateRequest;


public class CreateRequestHandler extends AbstractClientRequestHandler<CreateRequest> {
	@Override
	public void handleClientProtocolBuffersRequest(IGameUser iGameUser, CreateRequest request) {
		CObject cObject = CObject.newInstance();
		cObject.putUtfString("hello","world");
//		cObject.putClass("IGameUser",iGameUser);
		AppContext.getGameWorld().sendMessageToIService(AllianceService.class,cObject);

		AppContext.getGameWorld().sendMessageToLocalIService(AllianceService.class,cObject);

		AppContext.getGameWorld().sendMessageToIService(AllianceService.class.getName()+".1",cObject);

		AppContext.getGameWorld().sendMessageToIService(AllianceService.class.getName()+".2",cObject);

		AppContext.getGameWorld().sendMessageToIService(AllianceService.class.getName()+".5",cObject);
	}
}