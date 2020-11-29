package com.codebroker.extensions.service;

import com.codebroker.api.internal.IPacket;

import com.codebroker.api.internal.IResultStatusMessage;
import com.codebroker.api.internal.IService;
import com.codebroker.extensions.request.ClientHandlerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * 抽象Servie
 * @param <T>
 */
public abstract class AbstractIService<T> implements IService <IPacket<T>, IResultStatusMessage>
{
	private ClientHandlerFactory clientHandlerFactory=new ClientHandlerFactory<Integer,IServiceClientRequestHandler>();
	private Logger logger = LoggerFactory.getLogger(AbstractIService.class);


	public void addRequestHandler(T requestId, Class<? extends IServiceClientRequestHandler> theClass) {
		if (!(IServiceClientRequestHandler.class).isAssignableFrom(theClass)) {
			return;
		} else {
			logger.info("add handler id {} class {}", requestId, theClass);
			clientHandlerFactory.addHandler(requestId, theClass);
		}
	}

	protected void addRequestHandler(T requestId, IServiceClientRequestHandler requestHandler) {
		clientHandlerFactory.addHandler(requestId, requestHandler);
	}

	protected void removeRequestHandler(T requestId) {
		clientHandlerFactory.removeHandler(requestId);
	}

	protected void clearAllHandlers() {
		clientHandlerFactory.clearAll();
	}


	@Override
	public void handleMessage(IPacket<T> requestKeyMessage) {
		logger.info("handleMessage {}", requestKeyMessage);
		Optional<IServiceClientRequestHandler> handlerKey = clientHandlerFactory.findHandler(requestKeyMessage.getOpCode());
		if (handlerKey.isPresent()) {
			handlerKey.get().handleBackMessage(requestKeyMessage.getRawData());
		}else {
			logger.error("key is "+requestKeyMessage.getOpCode(),"No handler");
		}
	}

	@Override
	public IResultStatusMessage handleBackMessage(IPacket<T> requestKeyMessage) {
		logger.info("handleBackMessage {}", requestKeyMessage);
		Optional<IServiceClientRequestHandler> handlerKey = clientHandlerFactory.findHandler(requestKeyMessage.getOpCode());
		if (handlerKey.isPresent()) {
			Object handleBackMessage = handlerKey.get().handleBackMessage(requestKeyMessage.getRawData());
			return ResultStatusMessage.OK(handleBackMessage);
		} else {
			return ResultStatusMessage.ERROR("No handler");
		}
	}

}
