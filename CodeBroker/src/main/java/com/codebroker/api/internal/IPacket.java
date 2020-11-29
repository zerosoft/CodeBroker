package com.codebroker.api.internal;


public interface IPacket {
	/**
	 * 获取操作码.
	 *
	 * @return the op code
	 */
	int getOpCode();

	/**
	 * 获得消息体内容
	 * @return
	 */
	Object getRawData();
}
