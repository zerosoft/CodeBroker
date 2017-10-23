package com.avic.client;

import com.alibaba.fastjson.JSONObject;
import com.codebroker.protocol.BaseByteArrayPacket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ClientHandler extends ChannelInboundHandlerAdapter{

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		super.channelRegistered(ctx);
	}
	
	

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
	}



	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		super.channelRead(ctx, msg);
		if (msg instanceof BaseByteArrayPacket) {
			BaseByteArrayPacket arrayPacket=(BaseByteArrayPacket) msg;
			byte[] rawData = arrayPacket.getRawData();
			String text = new String(rawData);
			System.out.println(text);
			System.out.println(arrayPacket.getOpCode()+"=="+JSONObject.parse(text));
		}
		
	}



	@Override
	public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
		super.channelWritabilityChanged(ctx);
	}

	
}
