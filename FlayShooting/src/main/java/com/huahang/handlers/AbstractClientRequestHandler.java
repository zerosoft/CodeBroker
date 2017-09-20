package com.huahang.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codebroker.api.IClientRequestHandler;
import com.codebroker.api.IUser;
import com.codebroker.api.JavaProtocolTransform;
import com.google.common.base.Optional;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * 客户端请求处理的核心类以PB为例，可根据需求调整
 */
public abstract class AbstractClientRequestHandler implements IClientRequestHandler {
	private static final Logger logger = LoggerFactory.getLogger(AbstractClientRequestHandler.class);

	/**
	 * 客户端请求总入口
	 */
	public void handleClientRequest(IUser user, Object params) {
		
		JavaProtocolTransform message=null;
		try {
			message = bytesToProtocol(((byte[])params));
		} catch (InvalidProtocolBufferException e) {
			
		}
		//参数验证不过
		if (!verifyParams(user, message)) {
			
		}
		//处理收到的消息
		JavaProtocolTransform handleRequest = handleRequest(user, message);
		//空消息就不回復
		if (handleRequest==null) {
			return;
		}
		//发消息
		Integer requestid = getRequestId(handleRequest.getClass()).get();
		sendResponse(requestid, handleRequest, user);
		//处理发送完
		afterRequest(user, message, handleRequest);
	}

	/**
	 * 参数转换
	 * @param bs
	 * @return
	 * @throws InvalidProtocolBufferException
	 */
	public abstract JavaProtocolTransform bytesToProtocol(byte[] bs) throws InvalidProtocolBufferException;

	/**
	 * 验证前台参数是否合法
	 *
	 * @param user
	 * @param params
	 * @return
	 */
	public abstract boolean verifyParams(IUser user, JavaProtocolTransform params);

	/**
	 * 处理命令
	 *
	 * @param user
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public abstract JavaProtocolTransform handleRequest(IUser user, JavaProtocolTransform params);

	/**
	 * 命令执行结束后的处理
	 *
	 * @param user
	 * @param params
	 * @throws Exception
	 */
	public void afterRequest(IUser user, JavaProtocolTransform params, JavaProtocolTransform retObj) {
	}

	/**
	 * 返回数据
	 *
	 * @param cmdName
	 * @param retObj
	 * @param user
	 */
	public void sendResponse(int requestid, JavaProtocolTransform retObj, IUser user) {
		if (retObj == null) {
		}
		user.sendMessageToIoSession(requestid, retObj);
	}

	/**
	 * 命令ID
	 *
	 * @return
	 */
	public Optional<Integer> getRequestId(Class<? extends JavaProtocolTransform> clazz) {
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
