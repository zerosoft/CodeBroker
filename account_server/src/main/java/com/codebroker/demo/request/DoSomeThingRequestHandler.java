package com.codebroker.demo.request;

import com.codebroker.api.AppContext;
import com.codebroker.api.IClientRequestHandler;
import com.codebroker.api.IGameUser;
import com.codebroker.api.IGameWorld;
import com.codebroker.api.event.Event;
import com.codebroker.core.data.CObject;
import com.codebroker.protobuf.Login_C;

import java.util.Optional;

public class DoSomeThingRequestHandler implements IClientRequestHandler<Login_C> {
	@Override
	public void handleClientRequest(IGameUser user, Login_C message) {
		message.getDeviceId();
		message.getOpenId();

		String userId = user.getUserId();
		getClientRequestLogger().info("User Id {}",userId);

		user.sendMessageToIoSession(101,"hello world".getBytes());

//		Event event=new Event();
//		event.setTopic("login");
//		event.setMessage(CObject.newInstance());
//		user.dispatchEvent(event);

		IGameWorld gameWorld = AppContext.getGameWorld();
		Optional<IGameUser> iGameUserById = gameWorld.findIGameUserById("3451");
		if (iGameUserById.isPresent()){
			getClientRequestLogger().info("get it {}",iGameUserById.get().getUserId());
		}

		CObject object = CObject.newInstance();
		object.putUtfString("message","hello");
		object.putClass("IGame",user);
		AppContext.getGameWorld().sendMessageToService("ChatService", object);
	}
}

