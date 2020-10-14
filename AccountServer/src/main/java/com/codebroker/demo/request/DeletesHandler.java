package com.codebroker.demo.request;

import com.codebroker.api.IGameUser;
import com.codebroker.demo.AbstractClientRequestHandler;
import com.codebroker.protobuff.common.Deletes;

public class DeletesHandler extends AbstractClientRequestHandler<Deletes> {
	@Override
	public void handleClientProtocolBuffersRequest(IGameUser iGameUser, Deletes request) {
	}
}