package com.avic.sever.game.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codebroker.api.IClientRequestHandler;
import com.codebroker.api.IUser;
import com.google.common.base.Optional;

public abstract class AbstractClientRequestHandler implements IClientRequestHandler{
	private static final Logger logger = LoggerFactory.getLogger(AbstractClientRequestHandler.class);
	protected String userId;
	/**
	 * 客户端请求总入口
	 */
	public void handleClientRequest(IUser user, Object params) {
		
		String string=new String((byte[])params);
		//处理收到的消息
		String handleRequest = handleRequest(user,string.trim());
		//空消息就不回復
		if (handleRequest==null) {
			return;
		}
		//发消息
		Integer requestid = getRequestId(getClass()).get();
		sendResponse(requestid, handleRequest, user);
	}

	

	/**
	 * 处理命令
	 *
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public abstract String handleRequest(IUser user, String jsonString);


	/**
	 * 返回数据
	 *
	 * @param retObj
	 * @param user
	 */
	public void sendResponse(int requestid, String retObj, IUser user) {
		user.sendMessageToIoSession(requestid, retObj.getBytes());
	}

	/**
	 * 命令ID
	 *
	 * @return
	 */
	public Optional<Integer> getRequestId(Class clazz) {
		Integer reqId = null;
		try {
			reqId =clazz.getField("REQUEST_ID").getInt(null);
		} catch (IllegalAccessException e) {
			logger.error("get request id", e);
		} catch (NoSuchFieldException e) {
			logger.error("get request id", e);
		}
		return Optional.fromNullable(reqId);
	}


	/**
	 * 是否纪录log
	 *
	 * @return
	 */
	public boolean isLog() {
		return true;
	}
}
