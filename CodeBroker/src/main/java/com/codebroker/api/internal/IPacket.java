package com.codebroker.api.internal;


public interface IPacket<T> {
	/**
	 * 获取操作码.
	 *
	 * @return the op code
	 */
	T getOpCode();

	/**
	 * 获得消息体内容
	 * @return
	 */
	Object getRawData();
}
