package com.codebroker.demo.request;

import com.codebroker.api.IClientRequestHandler;
import com.codebroker.api.IGameUser;
import com.codebroker.protobuff.common.Updates;

public class UpdatesHandler implements IClientRequestHandler<Updates> {
	@Override
	public void handleClientRequest(IGameUser iGameUser, Updates request) {
	}
}