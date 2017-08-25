package com.codebroker.net.netty.filter;

import com.codebroker.protocol.BaseByteArrayPacket;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * The Class DataCodecEncoder.
 */
public class ByteArrayPacketCodecEncoder extends MessageToByteEncoder<BaseByteArrayPacket> {

	@Override
	protected void encode(ChannelHandlerContext ctx, BaseByteArrayPacket msg, ByteBuf out) throws Exception {
		byte[] binary = msg.toBinary();
		out.writeInt(binary.length);
		out.writeBytes(binary);
	}

}
