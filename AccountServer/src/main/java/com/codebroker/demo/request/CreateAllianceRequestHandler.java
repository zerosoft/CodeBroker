package com.codebroker.demo.request;

import com.codebroker.api.IClientRequestHandler;
import com.codebroker.api.IGameUser;
import com.codebroker.protobuff.alliance.CreateAllianceRequest;

public class CreateAllianceRequestHandler implements IClientRequestHandler<CreateAllianceRequest> {
	@Override
	public void handleClientRequest(IGameUser iGameUser, CreateAllianceRequest request) {
		java.lang.String  name = request.getName();
	}
}