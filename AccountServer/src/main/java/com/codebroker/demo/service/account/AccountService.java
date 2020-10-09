package com.codebroker.demo.service.account;

import com.codebroker.api.IGameUser;
import com.codebroker.api.annotation.IServerType;
import com.codebroker.api.internal.IService;
import com.codebroker.core.data.IObject;

@IServerType(cluster = true)
public class AccountService implements IService {

	@Override
	public void init(Object o) {

	}

	@Override
	public void destroy(Object o) {

	}

	@Override
	public void handleMessage(IObject iObject) {
		IGameUser user = (IGameUser) iObject.getClass("user");
	}

	@Override
	public String getName() {
		return null;
	}
}
