package com.codebroker.tcp;

import com.codebroker.net.netty.filter.ByteArrayPacketCodecDecoder;
import com.codebroker.net.netty.filter.ByteArrayPacketCodecEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class ClientInitializer extends ChannelInitializer<SocketChannel> {


    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("MessageHead-decoder", new ByteArrayPacketCodecDecoder());
        pipeline.addLast("MessageHead-encoder", new ByteArrayPacketCodecEncoder());
        pipeline.addLast("handler", new ClientHandler());
    }

}
