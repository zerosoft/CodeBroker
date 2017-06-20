package com.avalon.io.tcp;

import com.codebroker.net.netty.filter.MessageHeadDataCodecDecoder;
import com.codebroker.net.netty.filter.MessageHeadDataCodecEncoder;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class ClientInitializer extends ChannelInitializer<SocketChannel>{


	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast("MessageHead-decoder", new MessageHeadDataCodecDecoder());
		pipeline.addLast("MessageHead-encoder", new MessageHeadDataCodecEncoder());
		pipeline.addLast("handler", new ClientHandler());
	}

}
