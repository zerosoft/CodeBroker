package com.codebroker.demo;

import com.codebroker.api.AppContext;
import com.codebroker.api.IGameUser;
import com.codebroker.api.IGameWorld;
import com.codebroker.core.data.CObject;
import com.codebroker.demo.request.DoSomeThingRequestHandler;
import com.codebroker.demo.request.UserDisconnectionRequestHandler;
import com.codebroker.demo.service.AllianceService;
import com.codebroker.demo.service.ChatService;
import com.codebroker.demo.userevent.DoSameEvent;
import com.codebroker.exception.NoAuthException;
import com.codebroker.extensions.AppListenerExtension;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class DemoExtension extends AppListenerExtension {

	private Logger logger = LoggerFactory.getLogger(DemoExtension.class);

	private Map<String, String> userMap = new HashMap<>();
	//在线用户列表
	private Map<String, IGameUser> onlineUsers = Maps.newConcurrentMap();

	@Override
	public String sessionLoginVerification(String name, String parameter) throws NoAuthException {
		logger.info("handle login name {} parameter {}", name, parameter);
		return name;
	}

	@Override
	public void userLogin(IGameUser user) {
		logger.info("User Login parameter {}", user.getUserId());
		user.addEventListener("login", new DoSameEvent());
	}


	@Override
	public boolean handleLogout(IGameUser user) {
		logger.info("User LogOut parameter {}", user.getUserId());
		return false;
	}

	@Override
	public boolean userReconnection(IGameUser user) {
		return false;
	}

	@Override
	public void init(Object obj) {
		logger.info("Init");
		addRequestHandler(100, DoSomeThingRequestHandler.class);
		addRequestHandler(101, UserDisconnectionRequestHandler.class);

		IGameWorld gameWorld = AppContext.getGameWorld();
		boolean setManager = gameWorld.createGlobalService("AllianceService",new AllianceService());

//		AppContext.getGameWorld().getClusterService("AllianceService",new AllianceService());

//		System.out.println(setManager);
//		AppContext.setManager(new ChatService());

//		AllianceService manager = AppContext.getManager(AllianceService.class);
//		manager.init("hello world");

		boolean chatService = gameWorld.createGlobalService("ChatService", new ChatService());

		try {
			Thread.sleep(5000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		CObject object = CObject.newInstance();
		object.putUtfString("message", "hello");
		gameWorld.sendMessageToService("AllianceService", object);
	}
}
