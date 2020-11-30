package com.codebroker.demo.service.user.handler;

import com.codebroker.extensions.service.IServiceClientRequestHandler;

public class UserInit implements IServiceClientRequestHandler<String> {
	@Override
	public Object handleBackMessage(String o) {
		getClientRequestLogger().info("User init {}",o);
		return "SomeThing";
	}
}
