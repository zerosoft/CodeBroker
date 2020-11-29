package com.codebroker.extensions.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service的事件请求处理
 */
public interface IServiceClientRequestHandler<Message>{

	default Logger getClientRequestLogger() {
		return LoggerFactory.getLogger(IServiceClientRequestHandler.class);
	}

	Object handleBackMessage(Message message);

}
