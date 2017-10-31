package com.avic.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.alibaba.fastjson.JSONObject;
import com.avic.sever.game.handler.CommandID;
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
	            Channel ch = b.connect(HOST, 12345).sync().channel();

	            // Read commands from the stdin.
	            ChannelFuture lastWriteFuture = null;
	            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	            for (;;) {
	                String line = in.readLine();
	                if (line == null) {
	                    break;
	                }
	                BaseByteArrayPacket head=null;
	               //���ӷ�����
	                if (line.startsWith("1")) {
	                	JSONObject jsonObject=new JSONObject();
	                	jsonObject.put("name", "345");
	                	jsonObject.put("parm", "6789");
	                	head=new BaseByteArrayPacket(SystemRequest.USER_LOGIN_JSON.id,jsonObject.toString().getBytes());
					}
					if (line.startsWith("0")) {
						JSONObject jsonObject=new JSONObject();
						jsonObject.put("name", "345");
						jsonObject.put("parm", "6789");
						head=new BaseByteArrayPacket(SystemRequest.USER_LOGIN_REGEDIT.id,jsonObject.toString().getBytes());
					}
	                //������Ϸ
	                else if (line.startsWith("2")) {
						JSONObject jsonObject=new JSONObject();
	                	jsonObject.put("name", "1");
	                	byte[] bts=new byte[0];
	                	head=new BaseByteArrayPacket(CommandID.LOGIN,bts);	
					}
	                //��������
					else if (line.startsWith("3")) {
						JSONObject jsonObject=new JSONObject();
	                	jsonObject.put("name", "1");
	                	head=new BaseByteArrayPacket(CommandID.CREATE_ROOM,jsonObject.toString().getBytes());	
					}
	                //���뷿�� 4,1 ������id��
					else if (line.startsWith("4")) {
						String[] split = line.split(",");
						JSONObject jsonObject=new JSONObject();
	                	jsonObject.put("roomid", split[1]);
	                	head=new BaseByteArrayPacket(CommandID.JOIN_ROOM,jsonObject.toString().getBytes());	
					}
	                //��÷����б�
					else if (line.startsWith("5")) {
						JSONObject jsonObject=new JSONObject();
	                	jsonObject.put("name", "1");
	                	head=new BaseByteArrayPacket(CommandID.GET_ALL_ROOM,jsonObject.toString().getBytes());	
					}
	                //����������Ϣ
					else if (line.startsWith("6")) {
						JSONObject jsonObject=new JSONObject();
						String[] split = line.split(",");
	                	jsonObject.put("msg", split[1]);
	                	head=new BaseByteArrayPacket(CommandID.ROOM_CHAT_SEND,jsonObject.toString().getBytes());	
					}
	                //��������
					else if (line.startsWith("8")) {
						String[] split = line.split(",");
						JSONObject jsonObject=new JSONObject();
	                	jsonObject.put("msg", split[1]);
	                	head=new BaseByteArrayPacket(CommandID.WORD_CHAT_SEND,jsonObject.toString().getBytes());	
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
