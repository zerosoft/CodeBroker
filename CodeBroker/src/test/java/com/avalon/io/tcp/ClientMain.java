package com.avalon.io.tcp;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.codebroker.net.MessagePackImpl;
import com.codebroker.util.MessageHead;
import com.message.protocol.PBSystem.CS_USER_CONNECT_TO_SERVER;
import com.message.protocol.PBSystem.CS_USER_CONNECT_TO_SERVER.Builder;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class ClientMain {
	
    static final String HOST = System.getProperty("host", "127.0.0.1");
	
	 public static void main(String[] args) throws Exception {
	        EventLoopGroup group = new NioEventLoopGroup();
	        try {
	            Bootstrap b = new Bootstrap();
	            b.group(group)
	             .channel(NioSocketChannel.class)
	             .handler(new ClientInitializer());

	            // Start the connection attempt.
	            Channel ch = b.connect(HOST, 12345).sync().channel();

	            // Read commands from the stdin.
	            ChannelFuture lastWriteFuture = null;
	            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	            for (;;) {
	                String line = in.readLine();
	                if (line == null) {
	                    break;
	                }
	                MessageHead head=null;
	                if (line.startsWith("1")) {
						String[] split = line.split(" ");
						Builder newBuilder = CS_USER_CONNECT_TO_SERVER.newBuilder();
						newBuilder.setName(split[1]);
						newBuilder.setParams(split[2]);
						CS_USER_CONNECT_TO_SERVER build = newBuilder.build();
						head=new MessageHead(new MessagePackImpl(105,build.toByteArray()));
					}else if (line.startsWith("2")) {
//						String[] split = line.split(" ");
//						com.avalon.protocol.Chat.CS_CHAT_MESSAGE.Builder newBuilder = CS_CHAT_MESSAGE.newBuilder();
//						newBuilder.setName(split[1]);
//						newBuilder.setMessage(split[2]);
//						CS_CHAT_MESSAGE build = newBuilder.build();
//						head=new MessageHead(new MessagePackImpl(2,build.toByteArray()));	
					}
	                // Sends the received line to the server.
	                lastWriteFuture = ch.writeAndFlush(head);

	            }

	            // Wait until all messages are flushed before closing the channel.
	            if (lastWriteFuture != null) {
	                lastWriteFuture.sync();
	            }
	        } finally {
	            group.shutdownGracefully();
	        }
	    }
}
