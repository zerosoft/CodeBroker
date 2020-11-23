package com.codebroker.demo.request;

import com.codebroker.api.IGameUser;
import com.codebroker.demo.AbstractClientRequestHandler;
import com.codebroker.protobuff.common.ResponseCommon;

public class ResponseCommonHandler extends AbstractClientRequestHandler<ResponseCommon> {
	@Override
	public void handleClientProtocolBuffersRequest(IGameUser iGameUser, ResponseCommon request) {
		int  cmd = request.getCmd();
		com.codebroker.protobuff.common.Deletes  deletes = request.getDeletes();
		com.codebroker.protobuff.common.Creates  creates = request.getCreates();
		boolean  hasupdates = request.hasUpdates();
		boolean  hasdeletes = request.hasDeletes();
		com.codebroker.protobuff.common.Updates  updates = request.getUpdates();
		boolean  hascreates = request.hasCreates();
	}
}