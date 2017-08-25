package com.codebroker.protocol;

import java.nio.ByteBuffer;
/**
 * 封装成bytebuffer
 * @author zero
 *
 */
public class ByteBufferPacket extends BaseByteArrayPacket implements com.codebroker.api.internal.ByteBufferPacket{

	@Override
	public ByteBuffer toByteBuffer() {
		byte[] binary = toBinary();
		ByteBuffer buffer=ByteBuffer.allocate(binary.length);
		buffer.put(binary);
		return buffer;
	}

	@Override
	public void fromBuffer(ByteBuffer buffer) {
		byte[] binary = new byte[buffer.limit()];
		buffer.get(binary);
		fromBinary(binary);
	}

}
