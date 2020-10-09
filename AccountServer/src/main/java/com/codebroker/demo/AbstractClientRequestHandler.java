package com.codebroker.demo;

import com.codebroker.api.IClientRequestHandler;
import com.codebroker.api.IGameUser;
import com.codebroker.api.JavaProtocolTransform;
import com.google.protobuf.InvalidProtocolBufferException;

public abstract class AbstractClientRequestHandler<T> implements IClientRequestHandler<T> {

	@Override
	public void handleClientRequest(IGameUser gameUser, T message) {
		try {
			JavaProtocolTransform decode = decode(message);

			boolean pass = verifyParams(decode);

			if (pass) {
				handleClientRequest(gameUser, decode);
			} else {

			}
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 解码验证过得参数
	 *
	 * @param listener
	 * @param message
	 */
	public abstract void handleClientRequest(Object listener, JavaProtocolTransform message);

	/**
	 * 解码
	 *
	 * @param meObject
	 * @throws InvalidProtocolBufferException
	 */
	public abstract JavaProtocolTransform decode(Object meObject) throws InvalidProtocolBufferException;

	/**
	 * 参数的校对 子类有需求就叫对
	 */
	public boolean verifyParams(JavaProtocolTransform javaBean) {
		return true;
	}
}
