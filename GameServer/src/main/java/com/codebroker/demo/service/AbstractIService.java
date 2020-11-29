package com.codebroker.demo.service;

import com.codebroker.api.IClientRequestHandler;
import com.codebroker.api.internal.IRequestKeyMessage;
import com.codebroker.api.internal.IResultStatusMessage;
import com.codebroker.api.internal.IService;
import com.codebroker.api.internal.ResultStatusMessage;
import com.codebroker.core.data.CObject;
import com.codebroker.core.data.IObject;
import com.codebroker.extensions.request.ClientHandlerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Optional;

public abstract class AbstractIService<Integer> implements IService <IRequestKeyMessage<Integer>, IResultStatusMessage>{
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
	public void handleMessage(IRequestKeyMessage requestKeyMessage) {
		logger.info("handleMessage {}", requestKeyMessage);
		Optional<Object> handlerKey = clientHandlerFactory.findHandler((Integer) requestKeyMessage.getKey());
		if (handlerKey.isPresent()) {
			((IServiceClientRequestHandler) handlerKey.get()).handleBackMessage(this, requestKeyMessage.message());
		}else {
			logger.error("key is "+requestKeyMessage.getKey(),"No handler");
		}
	}

	@Override
	public IResultStatusMessage handleBackMessage(IRequestKeyMessage requestKeyMessage) {
		logger.info("handleBackMessage {}", requestKeyMessage);
		Optional<Object> handlerKey = clientHandlerFactory.findHandler(requestKeyMessage.getKey());
		if (handlerKey.isPresent()) {
			Object handleBackMessage = ((IServiceClientRequestHandler) handlerKey.get()).handleBackMessage(this, requestKeyMessage.message());
			return ResultStatusMessage.OK((Serializable) handleBackMessage);
		} else {
			return ResultStatusMessage.ERROR("No handler");

		}
	}

}
