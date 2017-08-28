package com.codebroker.net.netty;

import java.nio.ByteBuffer;

import org.apache.thrift.TException;

import com.codebroker.api.IoSession;
import com.codebroker.core.actor.SessionActor;
import com.codebroker.protocol.BaseByteArrayPacket;
import com.codebroker.protocol.ThriftSerializerFactory;
import com.codebroker.util.AkkaMediator;
import com.message.thrift.actor.ActorMessage;
import com.message.thrift.actor.Operation;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.actor.Props;
import io.netty.channel.ChannelHandlerContext;

/**
 * 网络和Actor的关联 负责TCP的手法消息
 * 
 * @author server
 *
 */
public class NettyIoSession implements IoSession {

	public static final String NAME = "NettyIoSession";

	final ChannelHandlerContext ctx;

	final long sessionId;

	private ActorRef actorRef;

	public NettyIoSession(ChannelHandlerContext ctx) {
		super();
		this.ctx = ctx;
		ActorSystem actorSystem = AkkaMediator.getActorSystem();
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
	private void sendMessageToTransport(Object msg) {
		if (msg instanceof BaseByteArrayPacket) {
			ActorMessage message=new ActorMessage();
			
			byte[] binary = ((BaseByteArrayPacket) msg).toBinary();
			ByteBuffer buffer=ByteBuffer.allocate(binary.length);
			buffer.put(binary);
			buffer.flip();
			
			message.messageRaw=buffer;
			message.op=Operation.SESSION_RECIVE_PACKET;
			byte[] actorMessage;
			try {
				actorMessage = ThriftSerializerFactory.getActorMessage(message);
				actorRef.tell(actorMessage, ActorRef.noSender());
			} catch (TException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
//			SessionActor.IosessionReciveMessage message = new SessionActor.IosessionReciveMessage((BaseByteArrayPacket) msg);
			
		}
	}

	public void processMessage(Object msg) {
		sendMessageToTransport(msg);
	}
}
