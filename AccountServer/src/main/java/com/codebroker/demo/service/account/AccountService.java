package com.codebroker.demo.service.account;

import com.codebroker.demo.service.account.request.LoginAccountHandler;
import com.codebroker.extensions.service.AbstractIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AccountService extends AbstractIService<Integer> {
	private Logger logger = LoggerFactory.getLogger(AccountService.class);

	@Override
	public void init(Object o) {
		logger.info("init");
		addRequestHandler(1, LoginAccountHandler.class);
	}

	@Override
	public void destroy(Object o) {

	}

	@Override
	public String getName() {
		return AccountService.class.getName();
	}
}
