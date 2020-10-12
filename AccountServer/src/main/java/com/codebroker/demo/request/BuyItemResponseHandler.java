package com.codebroker.demo.request;

import com.codebroker.api.IClientRequestHandler;
import com.codebroker.api.IGameUser;
import com.codebroker.protobuff.item.BuyItemResponse;

public class BuyItemResponseHandler implements IClientRequestHandler<BuyItemResponse> {
	@Override
	public void handleClientRequest(IGameUser iGameUser, BuyItemResponse request) {
		long  status = request.getStatus();
	}
}