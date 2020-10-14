package com.codebroker.demo.request;

import com.codebroker.api.IGameUser;
import com.codebroker.demo.AbstractClientRequestHandler;
import com.codebroker.protobuff.user.CreateResponse;

public class CreateResponseHandler extends AbstractClientRequestHandler<CreateResponse> {
	@Override
	public void handleClientProtocolBuffersRequest(IGameUser iGameUser, CreateResponse request) {
		long  status = request.getStatus();
	}
}