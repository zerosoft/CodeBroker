package com.codebroker.demo.service.user.handler;

import com.codebroker.extensions.service.IServiceClientRequestHandler;

public class UserLogin implements IServiceClientRequestHandler<String> {
	@Override
	public Object handleBackMessage(String o) {
		getClientRequestLogger().info("UserService User UserLogin {}",o);
		return null;
	}
}
