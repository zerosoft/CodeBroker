package com.codebroker.demo.service;

import com.codebroker.api.IClientRequestHandler;
import com.codebroker.api.internal.IService;
import com.codebroker.core.data.CObject;
import com.codebroker.core.data.IObject;
import com.codebroker.extensions.request.ClientHandlerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public abstract class AbstractIService implements IService {
	private ClientHandlerFactory clientHandlerFactory=new ClientHandlerFactory();
	private Logger logger = LoggerFactory.getLogger(AbstractIService.class);


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
		logger.info("handleMessage {}", iObject);
		Optional<Object> handlerKey = clientHandlerFactory.findHandler(iObject.getInt("handlerKey"));
		if (handlerKey.isPresent()) {
			((IServiceClientRequestHandler) handlerKey.get()).handleBackMessage(this, iObject);
		}else {
			logger.error(iObject.getDump(),"No handler");
		}
	}

	@Override
	public IObject handleBackMessage(IObject iObject) {
		logger.info("handleBackMessage {}", iObject);
		Optional<Object> handlerKey = clientHandlerFactory.findHandler(iObject.getInt("handlerKey"));
		if (handlerKey.isPresent()) {
			return ((IServiceClientRequestHandler) handlerKey.get()).handleBackMessage(this, iObject);
		} else {
			CObject object = CObject.newInstance();
			object.putUtfString("M#ERROR", "No handler");
			return object;

		}
	}

}
