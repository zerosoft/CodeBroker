package com.codebroker.net.netty;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import com.codebroker.api.IoSession;
import com.codebroker.api.internal.IBindingActor;
import com.codebroker.core.ContextResolver;
import com.codebroker.core.actortype.message.IWorldMessage;
import com.codebroker.core.actortype.message.ISession;
import com.codebroker.protocol.BaseByteArrayPacket;
import io.netty.channel.ChannelHandlerContext;

import java.nio.ByteBuffer;


/**
 * 网络和Actor的关联 负责TCP的收发消息
 *
 * @author LongJu
 */
public class NettyIoSession implements IoSession , IBindingActor<ISession> {

    final ChannelHandlerContext ctx;
    public ActorRef<ISession> sessionActorRef = null;

    public NettyIoSession(ChannelHandlerContext ctx) {
        super();
        this.ctx = ctx;
        ActorSystem<IWorldMessage> actorSystem = ContextResolver.getActorSystem();
        actorSystem.tell(new IWorldMessage.SessionOpen(this));
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

    @Override
    public boolean bindingActor(ActorRef<ISession> ref) {
        this.sessionActorRef = ref;
        return true;
    }
}
