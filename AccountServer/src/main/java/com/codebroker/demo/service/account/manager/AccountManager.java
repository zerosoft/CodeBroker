package com.codebroker.demo.service.account.manager;

import com.codebroker.demo.service.account.model.Account;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.AbstractService;

import java.util.Map;

public class AccountManager extends AbstractService {

	Map<String, Account> accountMap= Maps.newConcurrentMap();

	@Override
	protected void doStart() {

	}

	@Override
	protected void doStop() {

	}
}
