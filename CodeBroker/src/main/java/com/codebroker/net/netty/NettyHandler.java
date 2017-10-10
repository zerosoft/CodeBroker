package com.codebroker.net.netty;

import java.util.concurrent.atomic.AtomicInteger;

import com.codebroker.core.actor.CodeBrokerSystem;
import com.codebroker.core.data.CObject;
import com.codebroker.core.monitor.MonitorEventType;
import com.codebroker.util.AkkaMediator;
import com.codebroker.util.LogUtil;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class NettyHandler extends ChannelInboundHandlerAdapter {

	public static final String NAME = "NettyHandler";
	public static AtomicInteger sessionNum = new AtomicInteger(1);

	private NettyIoSession nettyIoSession;

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		super.channelRegistered(ctx);
		this.nettyIoSession = new NettyIoSession(ctx);
		
		CObject newInstance = CObject.newInstance();
		newInstance.putInt(MonitorEventType.KEY, MonitorEventType.SESSEION_ONLINE);
		AkkaMediator.getInbox().send(CodeBrokerSystem.monitorManager,newInstance);
		LogUtil.snedELKLogMessage(NAME, "SESSION NUM " + sessionNum.getAndIncrement());
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		nettyIoSession.processRecvieMessage(msg);
		LogUtil.snedELKLogMessage(NAME, "Read Messaeg  session Id" + nettyIoSession.sessionId);
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		super.handlerRemoved(ctx);
		nettyIoSession.close(false);
		
		CObject newInstance = CObject.newInstance();
		newInstance.putInt(MonitorEventType.KEY, MonitorEventType.SESSEION_OUTLINE);
		AkkaMediator.getInbox().send(CodeBrokerSystem.monitorManager,newInstance);
		LogUtil.snedELKLogMessage(NAME, "SESSION NUM " + sessionNum.getAndDecrement());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		LogUtil.snedELKLogMessage(NAME, cause.getMessage());
	}

}
