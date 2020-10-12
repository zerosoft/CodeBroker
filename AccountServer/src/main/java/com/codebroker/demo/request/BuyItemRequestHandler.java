package com.codebroker.demo.request;

import com.codebroker.api.IClientRequestHandler;
import com.codebroker.api.IGameUser;
import com.codebroker.protobuff.item.BuyItemRequest;

public class BuyItemRequestHandler implements IClientRequestHandler<BuyItemRequest> {
	@Override
	public void handleClientRequest(IGameUser iGameUser, BuyItemRequest request) {
		int  itemid = request.getItemId();
	}
}