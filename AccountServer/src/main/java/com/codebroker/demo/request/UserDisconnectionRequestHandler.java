package com.codebroker.demo.request;

import com.codebroker.api.IClientRequestHandler;
import com.codebroker.api.IGameUser;
import com.codebroker.protobuff.user.CreateRequest;

public class UserDisconnectionRequestHandler implements IClientRequestHandler<CreateRequest> {
//	@Override
//	public void handleClientRequest(IGameUser user, Object message) {
//		user.disconnect();
//	}

	@Override
	public void handleClientRequest(IGameUser iGameUser, CreateRequest createRequest) {

	}
}
