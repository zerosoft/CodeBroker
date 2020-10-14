package com.codebroker.demo.request;

import com.codebroker.api.IGameUser;
import com.codebroker.demo.AbstractClientRequestHandler;
import com.codebroker.protobuff.alliance.CreateAllianceRequest;

public class CreateAllianceRequestHandler extends AbstractClientRequestHandler<CreateAllianceRequest> {
	@Override
	public void handleClientProtocolBuffersRequest(IGameUser iGameUser, CreateAllianceRequest request) {
		java.lang.String  name = request.getName();
	}
}