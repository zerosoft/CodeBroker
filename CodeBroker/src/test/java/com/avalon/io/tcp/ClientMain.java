package com.avalon.io.tcp;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.codebroker.net.MessagePackImpl;
import com.codebroker.util.MessageHead;
import com.message.protocol.Message;
import com.message.protocol.PBGame.CS_REGISTER;
import com.message.protocol.PBSystem.CS_USER_CONNECT_TO_SERVER;

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
	                	CS_USER_CONNECT_TO_SERVER.Builder builder=CS_USER_CONNECT_TO_SERVER.newBuilder();
	                	builder.setName("Test");
	                	builder.setParams("Param");
	                	CS_USER_CONNECT_TO_SERVER build = builder.build();
	                	head=new MessageHead(new MessagePackImpl(Message.PB.SystemKey.CS_USER_CONNECT_TO_SERVER_VALUE,build.toByteArray()));	
					}else if (line.startsWith("2")) {
						CS_REGISTER register=CS_REGISTER.newBuilder().setName("1").setPassword("1").build();
						head=new MessageHead(new MessagePackImpl(Message.PB.MessageKey.CS_REGISTER_VALUE,register.toByteArray()));
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
