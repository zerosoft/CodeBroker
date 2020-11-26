package com.codebroker.net.netty;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Scheduler;
import akka.actor.typed.javadsl.AskPattern;
import com.codebroker.api.IoSession;
import com.codebroker.core.ContextResolver;
import com.codebroker.core.actortype.message.IGameRootSystemMessage;
import com.codebroker.core.actortype.message.ISession;
import com.codebroker.protocol.BaseByteArrayPacket;
import com.codebroker.setting.SystemEnvironment;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.concurrent.CompletionStage;


/**
 * 网络和Actor的关联 负责TCP的收发消息
 *
 * @author LongJu
 */
public class NettyIoSession implements IoSession {

    private Logger logger= LoggerFactory.getLogger(NettyIoSession.class);

    final ChannelHandlerContext ctx;
    public ActorRef<ISession> sessionActorRef = null;

    public NettyIoSession(ChannelHandlerContext ctx) {
        super();
        this.ctx = ctx;
        ActorSystem<IGameRootSystemMessage> actorSystem = ContextResolver.getActorSystem();

        //同步等待Actor绑定
        Scheduler scheduler = ContextResolver.getActorSystem().scheduler();
        CompletionStage<IGameRootSystemMessage.Reply> result =
                AskPattern.ask(
                        actorSystem,
                        replyTo -> new IGameRootSystemMessage.SessionOpen(replyTo, this),
                        Duration.ofMillis(SystemEnvironment.TIME_OUT_MILLIS),
                        scheduler);
        result.handle((reply, throwable) -> {
            if (reply instanceof IGameRootSystemMessage.SessionOpenReply) {
                sessionActorRef = ((IGameRootSystemMessage.SessionOpenReply) reply).sessionActorRef;
            } else {
                logger.error("bind actor error");
                this.close(true);
            }
            return null;
        });
    }

    @Override
    public void write(Object msg) {
        if (msg instanceof BaseByteArrayPacket) {
            ctx.writeAndFlush(msg);
        }
    }

    @Override
    public void write(Object msg, boolean flush) {
        if (flush){
            if (msg instanceof BaseByteArrayPacket) {
                ctx.writeAndFlush(msg);
            }
        }else {
            ctx.write(msg);
        }
    }

    @Override
    public boolean isConnection() {
        return ctx.channel().isActive();
    }

    @Override
    public void close(boolean close) {
        if (close) {
            if (ctx != null) {
                ctx.close();
                sessionActorRef=null;
            }
        } else {
            if (sessionActorRef!=null){
                sessionActorRef.tell(new ISession.SessionClose(false));
            }
        }
    }

    /**
     * 读取网络消息并发送给Actor
     *
     * @param msg
     */
    public void sessionReadMessage(Object msg) {
        if (msg instanceof BaseByteArrayPacket) {

            byte[] binary = ((BaseByteArrayPacket) msg).toBinary();
            ByteBuffer buffer = ByteBuffer.allocate(binary.length);
            buffer.put(binary);
            buffer.flip();

            sessionActorRef.tell(new ISession.SessionAcceptRequest((BaseByteArrayPacket) msg));

        }
    }

}
