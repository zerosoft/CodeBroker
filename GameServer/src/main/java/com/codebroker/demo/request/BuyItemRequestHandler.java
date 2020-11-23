package com.codebroker.demo.request;

import com.codebroker.api.IGameUser;
import com.codebroker.demo.AbstractClientRequestHandler;
import com.codebroker.protobuff.item.BuyItemRequest;

public class BuyItemRequestHandler extends AbstractClientRequestHandler<BuyItemRequest> {
	@Override
	public void handleClientProtocolBuffersRequest(IGameUser iGameUser, BuyItemRequest request) {
		int  itemid = request.getItemId();
	}
}