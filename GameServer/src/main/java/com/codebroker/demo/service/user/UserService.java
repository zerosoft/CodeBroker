package com.codebroker.demo.service.user;

import com.codebroker.demo.service.user.handler.UserInit;
import com.codebroker.demo.service.user.handler.UserLogin;
import com.codebroker.demo.service.user.handler.UserLogout;
import com.codebroker.extensions.service.AbstractIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService  extends AbstractIService<Integer> {
	private Logger logger= LoggerFactory.getLogger(UserService.class);
	@Override
	public void init(Object o) {
		logger.info("init UserService Service "+o);
		addRequestHandler(1,new UserInit());
		addRequestHandler(2,new UserLogin());
		addRequestHandler(3,new UserLogout());
	}

	@Override
	public void destroy(Object o) {

	}

	@Override
	public String getName() {
		return UserService.class.getName();
	}
}
