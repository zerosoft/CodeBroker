package com.codebroker.demo;

import com.codebroker.api.AppContext;
import com.codebroker.api.IGameUser;
import com.codebroker.api.IGameWorld;
import com.codebroker.demo.service.account.AccountService;
import com.codebroker.exception.NoAuthException;
import com.codebroker.extensions.AppListenerExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccountExtension extends AppListenerExtension {

	private Logger logger = LoggerFactory.getLogger(AccountExtension.class);



	@Override
	public String sessionLoginVerification(byte[] bytes) throws NoAuthException {
		throw new NoAuthException();
	}

	@Override
	public void userLogin(IGameUser user) {
		logger.info("User Login parameter {}", user.getUserId());
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
		logger.info("Init start");

		IGameWorld gameWorld = AppContext.getGameWorld();
		gameWorld.createClusterService(AccountService.class.getName(),new AccountService());

		logger.info("Init End");
	}
}
