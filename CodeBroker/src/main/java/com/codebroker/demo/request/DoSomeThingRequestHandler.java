package com.codebroker.demo.request;

import com.codebroker.api.AppContext;
import com.codebroker.api.IClientRequestHandler;
import com.codebroker.api.IGameUser;
import com.codebroker.api.IGameWorld;
import com.codebroker.api.event.Event;
import com.codebroker.core.data.CObject;

import java.util.Optional;

public class DoSomeThingRequestHandler implements IClientRequestHandler {
	@Override
	public void handleClientRequest(IGameUser user, Object message) {
		String userId = user.getUserId();
		getClientRequestLogger().info("User Id {}",userId);

		user.sendMessageToIoSession(101,"hello world".getBytes());

		Event event=new Event();
		event.setTopic("login");
		event.setMessage(CObject.newInstance());
		user.dispatchEvent(event);

		IGameWorld gameWorld = AppContext.getGameWorld();
		Optional<IGameUser> iGameUserById = gameWorld.findIGameUserById("3451");
		if (iGameUserById.isPresent()){
			getClientRequestLogger().info("get it {}",iGameUserById.get().getUserId());
		}
	}
}

