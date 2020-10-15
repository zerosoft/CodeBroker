package com.codebroker.demo.request;

import com.codebroker.api.AppContext;
import com.codebroker.api.IGameUser;
import com.codebroker.core.data.CObject;
import com.codebroker.demo.AbstractClientRequestHandler;
import com.codebroker.protobuff.user.CreateRequest;

public class CreateRequestHandler extends AbstractClientRequestHandler<CreateRequest> {
	@Override
	public void handleClientProtocolBuffersRequest(IGameUser iGameUser, CreateRequest request) {
		java.lang.String  name = request.getName();
		for (int i = 0; i < 10; i++) {
			CObject cObject = CObject.newInstance();
			cObject.putUtfString("name",name);
			cObject.putInt("id",i);
			AppContext.getGameWorld().sendMessageToService("AccountService-"+i,cObject);
		}

	}
}