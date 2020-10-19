package com.codebroker.demo.service;

import com.codebroker.api.IClientRequestHandler;
import com.codebroker.api.internal.IService;
import com.codebroker.core.data.CObject;
import com.codebroker.core.data.IObject;
import com.codebroker.demo.service.account.AccountService;
import com.codebroker.extensions.request.ClientHandlerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractIService implements IService {
	private ClientHandlerFactory clientHandlerFactory=new ClientHandlerFactory();
	private Logger logger = LoggerFactory.getLogger(AccountService.class);


	public void addRequestHandler(int requestId, Class<?> theClass) {
		if (!(IServiceClientRequestHandler.class).isAssignableFrom(theClass)) {
			return;
		} else {
			logger.info("add handler id {} class {}", requestId, theClass);
			clientHandlerFactory.addHandler(requestId, theClass);
		}
	}

	protected void addRequestHandler(int requestId, IClientRequestHandler requestHandler) {
		clientHandlerFactory.addHandler(requestId, requestHandler);
	}

	protected void removeRequestHandler(int requestId) {
		clientHandlerFactory.removeHandler(requestId);
	}

	protected void clearAllHandlers() {
		clientHandlerFactory.clearAll();
	}


	@Override
	public void handleMessage(IObject iObject) {
		logger.info("handleMessage {}",iObject);
		try {
			IServiceClientRequestHandler handlerKey = (IServiceClientRequestHandler) clientHandlerFactory.findHandler(iObject.getInt("handlerKey"));
			handlerKey.handleClientRequest(this,iObject);
		} catch (InstantiationException |IllegalAccessException e) {
			logger.error(iObject.getDump(),e);
		}
	}

	@Override
	public IObject handleBackMessage(IObject iObject) {
		logger.info("handleBackMessage {}",iObject);
		try {
			IServiceClientRequestHandler handlerKey = (IServiceClientRequestHandler) clientHandlerFactory.findHandler(iObject.getInt("handlerKey"));
			return handlerKey.handleBackMessage(this,iObject);
		} catch (InstantiationException |IllegalAccessException e) {
			logger.error(iObject.getDump(),e);
			CObject object = CObject.newInstance();
			object.putUtfString("M#ERROR",e.getMessage());
			return object;

		}
	}

}
