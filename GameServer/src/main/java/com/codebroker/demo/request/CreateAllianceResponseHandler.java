package com.codebroker.demo.request;

import com.codebroker.api.IGameUser;
import com.codebroker.demo.AbstractClientRequestHandler;
import com.codebroker.protobuff.alliance.CreateAllianceResponse;

public class CreateAllianceResponseHandler extends AbstractClientRequestHandler<CreateAllianceResponse> {
	@Override
	public void handleClientProtocolBuffersRequest(IGameUser iGameUser, CreateAllianceResponse request) {
		long  status = request.getStatus();
	}
}