package com.codebroker.demo.service;

import com.codebroker.api.internal.IService;
import com.codebroker.core.data.CObject;
import com.codebroker.core.data.CObjectLite;
import com.codebroker.core.data.IObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service的事件请求处理
 */
public interface IServiceClientRequestHandler {

	default Logger getClientRequestLogger() {
		return LoggerFactory.getLogger(IServiceClientRequestHandler.class);
	}

	void handleClientRequest(IService service, IObject message);

	default IObject handleBackMessage(IService service, IObject message){
		CObject cObject = CObjectLite.newInstance();
		cObject.putUtfString("msg","your need overwrite this");
		return cObject;
	}

}
