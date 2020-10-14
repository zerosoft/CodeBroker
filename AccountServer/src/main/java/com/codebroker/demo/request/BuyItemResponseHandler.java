package com.codebroker.demo.request;

import com.codebroker.api.IGameUser;
import com.codebroker.demo.AbstractClientRequestHandler;
import com.codebroker.protobuff.item.BuyItemResponse;

public class BuyItemResponseHandler extends AbstractClientRequestHandler<BuyItemResponse> {
	@Override
	public void handleClientProtocolBuffersRequest(IGameUser iGameUser, BuyItemResponse request) {
		long  status = request.getStatus();
	}
}