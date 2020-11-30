package com.codebroker.demo.userevent;

import com.codebroker.api.IGameUser;
import com.codebroker.api.event.IGameUserEventListener;

public class UserLoginEvent implements IGameUserEventListener<Object> {

	@Override
	public void handleEvent(IGameUser gameUser, Object event) {
		getGameUserEventListenerLogger().info("get Event game user id  {}",gameUser.getUserId());
	}
}
