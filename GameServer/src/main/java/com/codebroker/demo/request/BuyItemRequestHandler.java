package com.codebroker.demo.request;

import com.codebroker.api.IGameUser;
import com.codebroker.demo.AbstractClientRequestHandler;
import com.codebroker.protobuff.item.BuyItemRequest;

import java.util.Random;

public class BuyItemRequestHandler extends AbstractClientRequestHandler<BuyItemRequest> {
	@Override
	public void handleClientProtocolBuffersRequest(IGameUser iGameUser, BuyItemRequest request) {
		Random random=new Random();
		try {
			//模拟测试业务消耗时间
			Thread.sleep(random.nextInt(100));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}