package com.codebroker.demo.service.item.handler;

import com.codebroker.extensions.service.IServiceClientRequestHandler;

public class UserLogin implements IServiceClientRequestHandler<String> {
	@Override
	public Object handleBackMessage(String o) {
		getClientRequestLogger().info("ItemService User UserLogin {}",o);
		return null;
	}
}
