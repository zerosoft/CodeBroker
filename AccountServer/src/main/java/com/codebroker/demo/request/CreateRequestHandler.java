package com.codebroker.demo.request;

import com.codebroker.api.IClientRequestHandler;
import com.codebroker.api.IGameUser;
import com.codebroker.protobuff.user.CreateRequest;

public class CreateRequestHandler implements IClientRequestHandler<CreateRequest> {
	@Override
	public void handleClientRequest(IGameUser iGameUser, CreateRequest request) {
		java.lang.String  name = request.getName();
	}
}