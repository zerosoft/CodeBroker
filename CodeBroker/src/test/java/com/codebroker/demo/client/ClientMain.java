package com.codebroker.demo.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.codebroker.protocol.BaseByteArrayPacket;
import com.codebroker.setting.SystemRequest;

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
			Channel ch = b.connect(HOST, 22334).sync().channel();

			// Read commands from the stdin.
			ChannelFuture lastWriteFuture = null;
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			for (;;) {
				String line = in.readLine();
				if (line == null) {
					break;
				}
				BaseByteArrayPacket head=null;
				//登入或者注册
				if (line.startsWith(String.valueOf(SystemRequest.USER_LOGIN_OR_REGISTER.id))) {
//					JSONObject jsonObject=new JSONObject();
//					jsonObject.put("name", "3451");
//					jsonObject.put("parm", "6789");
//					head=new BaseByteArrayPacket(SystemRequest.USER_LOGIN_OR_REGISTER.id,jsonObject.toString().getBytes());
				}
				//主动断开连接
				if (line.startsWith("2")) {
//					JSONObject jsonObject=new JSONObject();
//					jsonObject.put("name", "3451");
//					head=new BaseByteArrayPacket(100,jsonObject.toString().getBytes());
				}
				//登入干一些事情
				else if (line.startsWith("3")) {
//					JSONObject jsonObject=new JSONObject();
//					jsonObject.put("name", "1");
//					head=new BaseByteArrayPacket(101,jsonObject.toString().getBytes());
				}
				//主动退出
				else if (line.startsWith("3")) {
//					JSONObject jsonObject=new JSONObject();
//					jsonObject.put("name", "1");
//					head=new BaseByteArrayPacket(101,jsonObject.toString().getBytes());
				}
				else if (line.startsWith("4")) {
//					String[] split = line.split(",");
//					JSONObject jsonObject=new JSONObject();
//					jsonObject.put("roomid", split[1]);
//					head=new BaseByteArrayPacket(3,jsonObject.toString().getBytes());
				}
				else if (line.startsWith("5")) {
//					JSONObject jsonObject=new JSONObject();
//					jsonObject.put("name", "1");
//					head=new BaseByteArrayPacket(4,jsonObject.toString().getBytes());
				}
				else if (line.startsWith("6")) {
//					JSONObject jsonObject=new JSONObject();
//					String[] split = line.split(",");
//					jsonObject.put("msg", split[1]);
//					head=new BaseByteArrayPacket(5,jsonObject.toString().getBytes());
				}
				else if (line.startsWith("8")) {
//					String[] split = line.split(",");
//					JSONObject jsonObject=new JSONObject();
//					jsonObject.put("msg", split[1]);
//					head=new BaseByteArrayPacket(6,jsonObject.toString().getBytes());
				}
				else if (line.startsWith("9")) {
//					JSONObject jsonObject=new JSONObject();
//					jsonObject.put("name", "飞行员1");
//					jsonObject.put("img", "头像1");
//					head=new BaseByteArrayPacket(7,jsonObject.toString().getBytes());
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
