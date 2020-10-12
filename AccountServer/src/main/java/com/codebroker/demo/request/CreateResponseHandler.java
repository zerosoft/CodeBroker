package com.codebroker.demo.request;

import com.codebroker.api.IClientRequestHandler;
import com.codebroker.api.IGameUser;
import com.codebroker.protobuff.user.CreateResponse;

public class CreateResponseHandler implements IClientRequestHandler<CreateResponse> {
	@Override
	public void handleClientRequest(IGameUser iGameUser, CreateResponse request) {
		long  status = request.getStatus();
	}
}