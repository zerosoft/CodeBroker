package com.codebroker.demo.request;

import com.codebroker.api.IClientRequestHandler;
import com.codebroker.api.IGameUser;
import com.codebroker.protobuff.alliance.CreateAllianceResponse;

public class CreateAllianceResponseHandler implements IClientRequestHandler<CreateAllianceResponse> {
	@Override
	public void handleClientRequest(IGameUser iGameUser, CreateAllianceResponse request) {
		long  status = request.getStatus();
	}
}