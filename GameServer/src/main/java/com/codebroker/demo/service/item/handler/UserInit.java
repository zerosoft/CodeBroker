package com.codebroker.demo.service.item.handler;

import com.codebroker.extensions.service.IServiceClientRequestHandler;

public class UserInit implements IServiceClientRequestHandler<String> {
	@Override
	public Object handleBackMessage(String o) {
		getClientRequestLogger().info("ItemService User init {}",o);
		return "ItemService SomeThing";
	}
}
