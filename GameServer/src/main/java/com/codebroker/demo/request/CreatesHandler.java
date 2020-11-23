package com.codebroker.demo.request;

import com.codebroker.api.IGameUser;
import com.codebroker.demo.AbstractClientRequestHandler;
import com.codebroker.protobuff.common.Creates;

public class CreatesHandler extends AbstractClientRequestHandler<Creates> {
	@Override
	public void handleClientProtocolBuffersRequest(IGameUser iGameUser, Creates request) {
	}
}