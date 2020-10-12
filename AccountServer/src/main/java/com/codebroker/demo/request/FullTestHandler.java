package com.codebroker.demo.request;

import com.codebroker.api.IClientRequestHandler;
import com.codebroker.api.IGameUser;
import com.codebroker.protobuff.user.FullTest;

public class FullTestHandler implements IClientRequestHandler<FullTest> {
	@Override
	public void handleClientRequest(IGameUser iGameUser, FullTest request) {
		java.lang.String  name = request.getName();
		int  id = request.getId();
		java.lang.String  email = request.getEmail();
		java.util.List  phoneslist = request.getPhonesList();
		int  phonescount = request.getPhonesCount();
	}
}