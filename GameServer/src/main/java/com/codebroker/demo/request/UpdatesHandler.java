package com.codebroker.demo.request;

import com.codebroker.api.IGameUser;
import com.codebroker.demo.AbstractClientRequestHandler;
import com.codebroker.protobuff.common.Updates;

public class UpdatesHandler extends AbstractClientRequestHandler<Updates> {
	@Override
	public void handleClientProtocolBuffersRequest(IGameUser iGameUser, Updates request) {
	}
}