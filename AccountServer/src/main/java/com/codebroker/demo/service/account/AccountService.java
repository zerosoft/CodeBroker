package com.codebroker.demo.service.account;

import com.codebroker.api.annotation.IServerClusterType;
import com.codebroker.core.data.CObject;
import com.codebroker.core.data.IObject;
import com.codebroker.demo.service.AbstractIService;
import com.codebroker.demo.service.account.request.LoginAccountHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@IServerClusterType(sharding = true)
public class AccountService extends AbstractIService {
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
	public IObject handleBackMessage(IObject iObject) {
		logger.info("AccountService handleBackMessage");
		IObject result= CObject.newInstance();
		result.putUtfString("uid", UUID.randomUUID().toString());
		return result;
	}

	@Override
	public String getName() {
		return AccountService.class.getName();
	}
}
