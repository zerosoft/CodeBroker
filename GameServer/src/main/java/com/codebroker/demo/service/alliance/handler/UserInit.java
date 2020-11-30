package com.codebroker.demo.service.alliance.handler;

import com.codebroker.extensions.service.IServiceClientRequestHandler;

public class UserInit implements IServiceClientRequestHandler<String> {
	@Override
	public Object handleBackMessage(String o) {
		getClientRequestLogger().info("User init {}",o);
		return null;
	}
}
