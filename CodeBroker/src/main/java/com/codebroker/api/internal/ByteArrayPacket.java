package com.codebroker.api.internal;

import java.nio.ByteBuffer;

/**
 * 网络封包 获取操作码及数据内容.
 *
 * @author ZERO
 */
public interface ByteArrayPacket{

	/**
	 * 获取操作码.
	 *
	 * @return the op code
	 */
	public int getOpCode();

	/**
	 * * 获得元数据.
	 *
	 * @return the raw data
	 */
	public byte[] getRawData();
	
	/**
	 * 获得序列化后的
	 * @return
	 */
	public byte[] toBinary();
	
	/**
	 * 序列化返回对象
	 * @param binary
	 */
	public void fromBinary(byte[] binary);
	
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
