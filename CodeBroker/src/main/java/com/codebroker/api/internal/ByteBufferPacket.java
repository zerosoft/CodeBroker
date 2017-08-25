package com.codebroker.api.internal;

import java.nio.ByteBuffer;

public interface ByteBufferPacket extends ByteArrayPacket{

	/**
	 * 获得序列化后的
	 * @return
	 */
	public ByteBuffer toByteBuffer();
	
	/**
	 * 序列化返回对象
	 * @param binary
	 */
	public void fromBuffer(ByteBuffer buffer);

}
