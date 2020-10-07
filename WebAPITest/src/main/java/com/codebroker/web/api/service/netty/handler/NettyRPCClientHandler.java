package com.codebroker.web.api.service.netty.handler;


import com.codebroker.web.api.service.api.IRegisty;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyRPCClientHandler extends ChannelInboundHandlerAdapter {
	private Logger logger= LoggerFactory.getLogger(NettyRPCClientHandler.class);
    private IRegisty registry;

	public NettyRPCClientHandler(IRegisty registry) {
		this.registry = registry;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		logger.info("channelActive"+ctx.name());
		registry.joining(ctx);
		super.channelActive(ctx);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		logger.info("channelInactive"+ctx.name());
		super.channelInactive(ctx);
	}

	@Override
	public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {
		registry.receiveMessage(ctx, msg);
		super.channelRead(ctx, msg);
		logger.info("channelRead"+ctx.name());
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
		super.channelReadComplete(ctx);
		logger.info("channelReadComplete"+ctx.name());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.error(cause.getMessage());
		registry.exception(ctx, cause);
		super.exceptionCaught(ctx, cause);
	}
	
	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		//网络没开启的情况
		registry.loseConnection(ctx);
		super.handlerRemoved(ctx);
		logger.info("handlerRemoved"+ctx.name());
	}

}
