package com.codebroker.demo.request;

import com.codebroker.api.IClientRequestHandler;
import com.codebroker.api.IGameUser;
import com.codebroker.protobuff.common.Creates;

public class CreatesHandler implements IClientRequestHandler<Creates> {
	@Override
	public void handleClientRequest(IGameUser iGameUser, Creates request) {
	}
}