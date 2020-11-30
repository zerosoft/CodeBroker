package com.codebroker.demo.userevent;

import com.codebroker.api.IGameUser;
import com.codebroker.api.event.IEvent;
import com.codebroker.api.event.IGameUserEventListener;

public class UserLostSessionEvent implements IGameUserEventListener<Object> {

	@Override
	public void handleEvent(IGameUser gameUser, Object event) {
		getGameUserEventListenerLogger().info("get UserRemoveEvent id  {}",gameUser.getUserId());
	}
}
