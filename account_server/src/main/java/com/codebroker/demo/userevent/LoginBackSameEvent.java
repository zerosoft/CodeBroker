package com.codebroker.demo.userevent;

import com.codebroker.api.IGameUser;
import com.codebroker.api.event.IEvent;
import com.codebroker.api.event.IGameUserEventListener;
import com.codebroker.core.data.IObject;

public class LoginBackSameEvent implements IGameUserEventListener {

	@Override
	public IObject handleEvent(IGameUser gameUser, IEvent event) {
		getGameUserEventListenerLogger().info("get server back Event game user id  {}",gameUser.getUserId());
		return null;
	}
}
