package com.codebroker.demo.service.alliance.handler;

import com.codebroker.extensions.service.IServiceClientRequestHandler;

public class UserLogin implements IServiceClientRequestHandler<String> {
	@Override
	public Object handleBackMessage(String o) {
		getClientRequestLogger().info("Alliance User UserLogin {}",o);
		return null;
	}
}
