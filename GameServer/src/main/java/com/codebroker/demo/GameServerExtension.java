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

import java.util.*;

public class GameServerExtension extends AppListenerExtension {

	private Logger logger = LoggerFactory.getLogger(GameServerExtension.class);

	private Map<String, String> userMap = new HashMap<>();
	//在线用户列表
	private Map<String, IGameUser> onlineUsers = Maps.newConcurrentMap();

	@Override
	public String sessionLoginVerification(String name, String parameter) throws NoAuthException {
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
		} else {
			throw new NoAuthException();
		}
		return uid;
	}

	@Override
	public void userLogin(IGameUser user) {
		logger.info("User Login parameter {}", user.getUserId());
		user.addEventListener("login", new DoSameEvent());
		user.addEventListener("USER_REMOVE", new UserRemoveEvent());
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

//		IGameWorld gameWorld = AppContext.getGameWorld();
//		boolean accountService = gameWorld.createClusterService(AccountService.class.getName(),new AccountService());
		addRequestHandler(11, CreateRequestHandler.class);
//		logger.info("Account Service create {}",accountService);
//		AccountDBManager accountDBManager =new AccountDBManager();
//		ArrayList<Service> serviceArrayList = Lists.newArrayList();
//		serviceArrayList.add(accountDBManager);
//		ServiceManager serviceManager=new ServiceManager(serviceArrayList);
//		serviceManager.startAsync();

	}
}
