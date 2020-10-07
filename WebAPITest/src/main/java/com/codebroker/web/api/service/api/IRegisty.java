package com.codebroker.web.api.service.api;

import io.netty.channel.ChannelHandlerContext;

/**
 * 远端RPC注册
 * @author ZERO
 *
 */
public interface IRegisty {
	
	//新连接的Session
	 void joining(ChannelHandlerContext ioSession);
	//异常处理
	 void exception(ChannelHandlerContext ioSession, Throwable cause);
	//失去网络
	 void loseConnection(ChannelHandlerContext ioSession);
	//接收消息
	 void receiveMessage(ChannelHandlerContext ioSession, Object msg);
}
