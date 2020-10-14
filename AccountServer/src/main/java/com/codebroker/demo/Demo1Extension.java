package com.codebroker.demo;

import com.codebroker.api.AppContext;
import com.codebroker.api.IGameUser;
import com.codebroker.api.IGameWorld;
import com.codebroker.core.data.CObject;
import com.codebroker.core.data.IObject;
import com.codebroker.demo.request.CreateRequestHandler;
import com.codebroker.demo.service.account.AccountService;
import com.codebroker.demo.userevent.DoSameEvent;
import com.codebroker.exception.NoAuthException;
import com.codebroker.extensions.AppListenerExtension;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class Demo1Extension extends AppListenerExtension {

	private Logger logger = LoggerFactory.getLogger(DemoExtension.class);

	private Map<String, String> userMap = new HashMap<>();
	//在线用户列表
	private Map<String, IGameUser> onlineUsers = Maps.newConcurrentMap();

	@Override
	public String sessionLoginVerification(String name, String parameter) throws NoAuthException {
		logger.info("handle login name {} parameter {}", name, parameter);
		AccountService manager = AppContext.getManager(AccountService.class);
//		AppContext.getGameWorld().sendMessageToService();
		CObject cObject = CObject.newInstance();
		cObject.putUtfString("name",name);
		cObject.putUtfString("password",parameter);
		IObject iObject =manager.handleBackMessage(cObject);
//		IObject iObject = AppContext.getGameWorld().sendMessageToServiceWithBack("AccountService", cObject);
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

		IGameWorld gameWorld = AppContext.getGameWorld();
		boolean accountService = gameWorld.createGlobalService("AccountService",new AccountService());

		addRequestHandler(11, CreateRequestHandler.class);
		logger.info("Account Service create {}",accountService);

	}
}
