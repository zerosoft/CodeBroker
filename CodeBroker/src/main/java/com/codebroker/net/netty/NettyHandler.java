package com.codebroker.net.netty;

import com.codebroker.core.data.CObject;
import com.codebroker.core.monitor.MonitorEventType;
import com.codebroker.util.LogUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *  @author LongJu
 */
public class NettyHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(NettyHandler.class);

    public static final String NAME = "NettyHandler";
    public static AtomicInteger sessionNum = new AtomicInteger(1);

    private NettyIoSession nettyIoSession;

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.nettyIoSession = new NettyIoSession(ctx);

        CObject newInstance = CObject.newInstance();
        newInstance.putInt(MonitorEventType.KEY, MonitorEventType.SESSEION_ONLINE);
        LogUtil.snedELKLogMessage(NAME, "SESSION NUM " + sessionNum.getAndIncrement());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        nettyIoSession.sessionReadMessage(msg);
        LogUtil.snedELKLogMessage(NAME, "Read Message session" );
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
        nettyIoSession.close(false);

        CObject newInstance = CObject.newInstance();
        newInstance.putInt(MonitorEventType.KEY, MonitorEventType.SESSEION_OUTLINE);
//        AkkaUtil.getInbox().send(CodeBrokerSystem.getInstance().getMonitorManager(), newInstance);
        LogUtil.snedELKLogMessage(NAME, "SESSION NUM " + sessionNum.getAndDecrement());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LogUtil.snedELKLogMessage(NAME, cause.getMessage());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            //服务端对应着读事件，当为READER_IDLE时触发
            IdleStateEvent event = (IdleStateEvent)evt;
            if(event.state() == IdleState.READER_IDLE){
//                logger.debug("不活动的链接 READER_IDLE");
            }else if(event.state() == IdleState.WRITER_IDLE){
//                logger.debug("不活动的链接 WRITER_IDLE");
            }
            else if(event.state() == IdleState.ALL_IDLE){
//                logger.debug("不活动的链接 ALL_IDLE");
            }
            else{
                super.userEventTriggered(ctx,evt);
            }
        }
    }
}
