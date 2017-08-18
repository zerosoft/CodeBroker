package com.codebroker.core.eventbus;

import java.nio.ByteBuffer;

import com.codebroker.core.eventbus.CodebrokerEnvelope.MsgEnvelope;

import akka.actor.ActorRef;
import akka.event.japi.LookupEventBus;

/**
 * 集群订阅事件
 * 
 * @author zero
 *
 */
public class CluserEnvelope extends LookupEventBus<MsgEnvelope, ActorRef, String> {

	public static class CluserMsgEnvelope {
		public final String topic;// 主题
		public final int cmd;// 命令
		public final ByteBuffer raw;// 元数据
		public final ActorRef sender;// 发送者

		public CluserMsgEnvelope(String topic, int cmd, ByteBuffer raw, ActorRef sender) {
			super();
			this.topic = topic;
			this.cmd = cmd;
			this.raw = raw;
			this.sender = sender;
		}

	}

	@Override
	public String classify(MsgEnvelope msgEnvelope) {
		return msgEnvelope.topic;
	}

	@Override
	public int compareSubscribers(ActorRef actorRef, ActorRef actorRef2) {
		return actorRef.compareTo(actorRef2);
	}

	@Override
	public int mapSize() {
		return 128;
	}

	@Override
	public void publish(MsgEnvelope event, ActorRef subscriber) {
		subscriber.tell(event.payload, ActorRef.noSender());
	}
}
