package com.codebroker.net.netty;

import com.codebroker.net.netty.filter.ByteArrayPacketCodecDecoder;
import com.codebroker.net.netty.filter.ByteArrayPacketCodecEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("Time-check", new IdleStateHandler(40, 50, 60, TimeUnit.SECONDS));
        pipeline.addLast("MessageHead-decoder", new ByteArrayPacketCodecDecoder());
        pipeline.addLast("MessageHead-encoder", new ByteArrayPacketCodecEncoder());
        pipeline.addLast("handler", new NettyHandler());
    }

}
