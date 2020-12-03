package com.codebroker.demo;

import com.codebroker.api.IClientRequestHandler;
import com.codebroker.api.IGameUser;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.InvalidProtocolBufferException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

/**
 * 基于Protobuff 的网络解码
 * @param <T>
 */
public abstract class  AbstractClientRequestHandler<T extends GeneratedMessageV3> implements IClientRequestHandler {

	Logger logger= LoggerFactory.getLogger(AbstractClientRequestHandler.class);

	@Override
	public void handleClientRequest(IGameUser gameUser, Object message) {
		try {
			T decode = decode(message);

			boolean pass = verifyParams(decode);

			if (pass) {
				handleClientProtocolBuffersRequest(gameUser, decode);
			} else {
				logger.error("not pass  verifyParams");
			}
		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
			getClientRequestLogger().error(e.getMessage());
		}
	}

	/**
	 * 解码验证过得参数
	 *
	 * @param listener
	 * @param message
	 */
	public abstract void handleClientProtocolBuffersRequest(IGameUser listener, T message);

	/**
	 * 解码
	 *
	 * @param meObject
	 * @throws InvalidProtocolBufferException
	 */
	public  <T extends GeneratedMessageV3> T decode(Object meObject) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		Class<T> tClass = (Class<T>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		Method parseFrom = tClass.getDeclaredMethod("parseFrom",byte[].class);
		Object invoke = parseFrom.invoke(null, meObject);
		return (T) invoke;
	}

	/**
	 * 参数的校对 子类有需求就使用校对参数
	 */
	public boolean verifyParams(T message) {
		return true;
	}


}
