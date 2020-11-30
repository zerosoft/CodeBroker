package com.codebroker.demo.service.item.handler;

import com.codebroker.extensions.service.IServiceClientRequestHandler;

public class UserLogout implements IServiceClientRequestHandler<String> {
	@Override
	public Object handleBackMessage(String o) {
		getClientRequestLogger().info("User UserLogout {}",o);
		return null;
	}
}
