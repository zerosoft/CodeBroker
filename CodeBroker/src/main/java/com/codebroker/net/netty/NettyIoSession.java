package com.codebroker.net.netty;

import com.codebroker.api.IoSession;
import com.codebroker.api.internal.IoMessagePackage;
import com.codebroker.core.actor.SessionActor;
import com.codebroker.util.AkkaMediator;
import com.codebroker.util.MessageHead;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.actor.Props;
import io.netty.channel.ChannelHandlerContext;

/**
 * 网络和Actor的关联
 * 负责TCP的手法消息
 * @author server
 *
 */
public class NettyIoSession implements IoSession {

	public static final String NAME="NettyIoSession";
	
	final ChannelHandlerContext ctx;

	final long sessionId;

	private ActorRef actorRef;

	public NettyIoSession(ChannelHandlerContext ctx) {
		super();
		this.ctx = ctx;
		ActorSystem actorSystem = AkkaMediator.getActorSystem();
		this.sessionId = NettyServerMonitor.sessionIds.getAndIncrement();
		actorRef = actorSystem.actorOf(Props.create(SessionActor.class, this),(NAME + "_" + sessionId));
	}

	public long getSessionId() {
		return sessionId;
	}

	@Override
	public void write(Object msg) {
		if (msg instanceof IoMessagePackage) {
			MessageHead head = new MessageHead((IoMessagePackage) msg);
			ctx.writeAndFlush(head);
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
		if (msg instanceof IoMessagePackage) {
			SessionActor.IosessionReciveMessage message = 
			new SessionActor.IosessionReciveMessage((IoMessagePackage) msg);
			actorRef.tell(message, ActorRef.noSender());
		}
	}

	public void processMessage(Object msg) {
		sendMessageToTransport(msg);
	}
}
