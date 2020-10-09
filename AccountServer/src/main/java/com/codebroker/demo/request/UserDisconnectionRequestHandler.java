package com.codebroker.demo.request;

import com.codebroker.api.IClientRequestHandler;
import com.codebroker.api.IGameUser;

public class UserDisconnectionRequestHandler implements IClientRequestHandler {
	@Override
	public void handleClientRequest(IGameUser user, Object message) {
		user.disconnect();
	}
}
