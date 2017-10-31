package com.codebroker.net.netty;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.actor.Props;
import com.codebroker.api.IoSession;
import com.codebroker.core.actor.CodeBrokerSystem;
import com.codebroker.core.actor.SessionActor;
import com.codebroker.core.data.CObject;
import com.codebroker.core.monitor.MonitorEventType;
import com.codebroker.protocol.BaseByteArrayPacket;
import com.codebroker.protocol.ThriftSerializerFactory;
import com.codebroker.util.AkkaUtil;
import com.message.thrift.actor.ActorMessage;
import com.message.thrift.actor.Operation;
import io.netty.channel.ChannelHandlerContext;
import org.apache.thrift.TException;

import java.nio.ByteBuffer;

/**
 * 网络和Actor的关联 负责TCP的手法消息
 *
 * @author server
 */
public class NettyIoSession implements IoSession {

    public static final String NAME = "NettyIoSession";

    final ChannelHandlerContext ctx;
    final long sessionId;
    private ThriftSerializerFactory thriftSerializerFactory = new ThriftSerializerFactory();
    private ActorRef actorRef;

    public NettyIoSession(ChannelHandlerContext ctx) {
        super();
        this.ctx = ctx;
        ActorSystem actorSystem = AkkaUtil.getActorSystem();
        this.sessionId = NettyServerMonitor.sessionIds.getAndIncrement();
        actorRef = actorSystem.actorOf(Props.create(SessionActor.class, this), (NAME + "_" + sessionId));
    }

    public long getSessionId() {
        return sessionId;
    }

    @Override
    public void write(Object msg) {
        if (msg instanceof BaseByteArrayPacket) {
            ctx.writeAndFlush(msg);

            CObject newInstance = CObject.newInstance();
            newInstance.putInt(MonitorEventType.KEY, MonitorEventType.SESSEION_WRITE_FLOW);
            newInstance.putLong(MonitorEventType.SESSION_ID, sessionId);
            newInstance.putDouble(MonitorEventType.SESSION_FLOW, ((BaseByteArrayPacket) msg).getRawData().length + 4);
            newInstance.putLong(MonitorEventType.SESSION_TIME, System.currentTimeMillis());
            AkkaUtil.getInbox().send(CodeBrokerSystem.getInstance().getMonitorManager(), newInstance);
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
            }
        } else {
            actorRef.tell(PoisonPill.getInstance(), ActorRef.noSender());
        }
    }

    /**
     * 发送消息
     *
     * @param msg
     */
    private void send2SessionActor(Object msg) {
        if (msg instanceof BaseByteArrayPacket) {
            ActorMessage message = new ActorMessage();

            byte[] binary = ((BaseByteArrayPacket) msg).toBinary();
            ByteBuffer buffer = ByteBuffer.allocate(binary.length);
            buffer.put(binary);
            buffer.flip();

            message.messageRaw = buffer;
            message.op = Operation.SESSION_RECIVE_PACKET;
            byte[] actorMessage;
            try {
                actorMessage = thriftSerializerFactory.getActorMessage(message);
                actorRef.tell(actorMessage, ActorRef.noSender());

                CObject newInstance = CObject.newInstance();
                newInstance.putInt(MonitorEventType.KEY, MonitorEventType.SESSEION_RECIVE_FLOW);
                newInstance.putLong(MonitorEventType.SESSION_ID, sessionId);
                newInstance.putDouble(MonitorEventType.SESSION_FLOW, binary.length);
                newInstance.putLong(MonitorEventType.SESSION_TIME, System.currentTimeMillis());
                AkkaUtil.getInbox().send(CodeBrokerSystem.getInstance().getMonitorManager(), newInstance);
            } catch (TException e) {
                e.printStackTrace();
            }

        }
    }

    public void processRecvieMessage(Object msg) {
        send2SessionActor(msg);
    }
}
