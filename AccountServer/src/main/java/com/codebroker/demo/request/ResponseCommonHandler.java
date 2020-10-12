package com.codebroker.demo.request;

import com.codebroker.api.IClientRequestHandler;
import com.codebroker.api.IGameUser;
import com.codebroker.protobuff.common.ResponseCommon;

public class ResponseCommonHandler implements IClientRequestHandler<ResponseCommon> {
	@Override
	public void handleClientRequest(IGameUser iGameUser, ResponseCommon request) {
		int  cmd = request.getCmd();
		boolean  hasdeletes = request.hasDeletes();
		com.codebroker.protobuff.common.Updates  updates = request.getUpdates();
		boolean  hascreates = request.hasCreates();
		boolean  hasupdates = request.hasUpdates();
		com.codebroker.protobuff.common.Creates  creates = request.getCreates();
		com.codebroker.protobuff.common.Deletes  deletes = request.getDeletes();
	}
}