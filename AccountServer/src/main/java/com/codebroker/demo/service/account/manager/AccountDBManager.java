package com.codebroker.demo.service.account.manager;

import com.codebroker.demo.service.account.model.Account;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.AbstractService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class AccountDBManager extends AbstractService {

	private Logger logger= LoggerFactory.getLogger(AccountDBManager.class);

	Map<String, Account> accountMap= Maps.newConcurrentMap();

	private static AccountDBManager instance;

	public static AccountDBManager getInstance() {
		return instance;
	}

	@Override
	protected void doStart() {
		logger.info("Start AccountManager");

		AccountDBManager.instance=this;
	}

	@Override
	protected void doStop() {
		logger.info("Stop AccountManager");
		AccountDBManager.instance=null;
	}
}
