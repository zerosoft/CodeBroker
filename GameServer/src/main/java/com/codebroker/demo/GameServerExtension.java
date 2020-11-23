package com.codebroker.demo;

import com.codebroker.api.AppContext;
import com.codebroker.api.IGameUser;
import com.codebroker.core.data.CObject;
import com.codebroker.core.data.IObject;
import com.codebroker.demo.request.CreateRequestHandler;
import com.codebroker.demo.userevent.DoSameEvent;
import com.codebroker.demo.userevent.UserRemoveEvent;
import com.codebroker.exception.NoAuthException;
import com.codebroker.extensions.AppListenerExtension;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.*;

public class GameServerExtension extends AppListenerExtension {

	private Logger logger = LoggerFactory.getLogger(GameServerExtension.class);

	private Map<String, String> userMap = new HashMap<>();
	//在线用户列表
	private Map<String, IGameUser> onlineUsers = Maps.newConcurrentMap();
	Gson gson=new Gson();

	@Override
	public String sessionLoginVerification(byte[] sourceProtocol) throws NoAuthException {
		JsonObject jsonElement = gson.fromJson(new String(sourceProtocol), JsonObject.class);
		String name=jsonElement.get("name").getAsString();
		String parameter=jsonElement.get("parm").getAsString();
		logger.info("handle login name {} parameter {}", name, parameter);
		CObject cObject = CObject.newInstance();
		cObject.putInt("handlerKey",1);
		cObject.putUtfString("name",name);
		cObject.putUtfString("password",parameter);

		Optional<IObject> iObject = AppContext.getGameWorld()
				.sendMessageToClusterIService("com.codebroker.demo.service.account.AccountService", cObject);
		String uid;
		if (iObject.isPresent()) {
			IObject iObject1 = iObject.get();
			uid = iObject1.getUtfString("uid");
			logger.info("user login {}",uid);

//			if (onlineUsers.containsKey(uid)){
//				onlineUsers.get(uid).disconnect();
//			}
			return uid;
		} else {
			throw new NoAuthException();
		}

	}

	@Override
	public void userLogin(IGameUser user) {
		logger.info("User Login parameter {}", user.getUserId());
		user.addEventListener("login", new DoSameEvent());
		user.addEventListener("USER_REMOVE", new UserRemoveEvent());
		onlineUsers.put(user.getUserId(),user);
	}


	@Override
	public boolean handleLogout(IGameUser user) {
		logger.info("User LogOut parameter {}", user.getUserId());
		onlineUsers.remove(user.getUserId());
		return false;
	}

	@Override
	public boolean userReconnection(IGameUser user) {
		return false;
	}

	@Override
	public void init(Object obj) {
		logger.info("Init");

		addRequestHandler(11, CreateRequestHandler.class);

	}
}