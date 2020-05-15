package com.codebroker.net.netty.filter;

import com.codebroker.protocol.BaseByteArrayPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 网络数据包解析 |4| |4| |……| 数据包长度 操作码 数据.
 *
 * @author LongJu
 */

public class ByteArrayPacketCodecDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 4) {
            return;
        }
        in.markReaderIndex();

        int dataLength = in.readInt();
//        System.out.println(dataLength + "======");
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }

        int opcode = in.readInt();
//        System.out.println("opcode=" + opcode);
        byte[] decoded = new byte[dataLength - 4];
        in.readBytes(decoded);

        BaseByteArrayPacket messagePack = new BaseByteArrayPacket(opcode, decoded);

        out.add(messagePack);

    }

}
