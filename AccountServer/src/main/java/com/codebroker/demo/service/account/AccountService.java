package com.codebroker.demo.service.account;

import com.codebroker.api.IGameUser;
import com.codebroker.api.annotation.IServerType;
import com.codebroker.api.internal.IService;
import com.codebroker.core.data.CObject;
import com.codebroker.core.data.IObject;
import com.codebroker.demo.DemoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@IServerType(cluster = true)
public class AccountService implements IService {
	private Logger logger = LoggerFactory.getLogger(AccountService.class);
	@Override
	public void init(Object o) {
		logger.info("init");
	}

	@Override
	public void destroy(Object o) {

	}

	@Override
	public void handleMessage(IObject iObject) {
		logger.info("handleMessage {}",iObject);
		IGameUser user = (IGameUser) iObject.getClass("user");
	}

	@Override
	public IObject  handleBackMessage(IObject obj) {
		logger.info("handleBackMessage {}",obj);
		CObject cObject = CObject.newInstance();
		cObject.putUtfString("uid",obj.getUtfString("name"));
		return cObject;
	}

	@Override
	public String getName() {
		return null;
	}
}
