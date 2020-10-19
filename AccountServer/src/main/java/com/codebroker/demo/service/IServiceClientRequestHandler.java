package com.codebroker.demo.service;

import com.codebroker.api.internal.IService;
import com.codebroker.core.data.CObjectLite;
import com.codebroker.core.data.IObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public interface IServiceClientRequestHandler {

	default Logger getClientRequestLogger() {
		return LoggerFactory.getLogger(IServiceClientRequestHandler.class);
	}

	default IObject handleBackMessage(IService service, IObject message){
		return CObjectLite.newInstance();
	}

	void handleClientRequest(IService service, IObject message);
}
