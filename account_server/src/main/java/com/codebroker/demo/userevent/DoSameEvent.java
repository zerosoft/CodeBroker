package com.codebroker.demo.userevent;

import com.codebroker.api.IGameUser;
import com.codebroker.api.event.Event;
import com.codebroker.api.event.IEvent;
import com.codebroker.api.event.IGameUserEventListener;

public class DoSameEvent implements IGameUserEventListener {

	@Override
	public void handleEvent(IGameUser gameUser, IEvent event) {
		getGameUserEventListenerLogger().info("get Event game user id  {}",gameUser.getUserId());
	}
}
