package com.codebroker.demo.request;

import com.codebroker.api.IClientRequestHandler;
import com.codebroker.api.IGameUser;
import com.codebroker.protobuff.common.Deletes;

public class DeletesHandler implements IClientRequestHandler<Deletes> {
	@Override
	public void handleClientRequest(IGameUser iGameUser, Deletes request) {
	}
}