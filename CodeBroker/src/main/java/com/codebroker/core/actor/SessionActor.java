package com.codebroker.core.actor;

import java.nio.ByteBuffer;

import org.apache.thrift.TDeserializer;
import org.apache.thrift.TSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codebroker.api.IUser;
import com.codebroker.api.IoSession;
import com.codebroker.core.actor.UserActor.Disconnect;
import com.codebroker.core.actor.UserActor.ReciveIosessionMessage;
import com.codebroker.core.actor.WorldActor.UserReconnectionTry;
import com.codebroker.protocol.BaseByteArrayPacket;
import com.codebroker.protocol.ByteBufferPacket;
import com.codebroker.protocol.ThriftSerializerFactory;
import com.codebroker.setting.SystemMessageType;
import com.google.protobuf.InvalidProtocolBufferException;
import com.message.protocol.Message;
import com.message.protocol.PBSystem.CS_USER_CONNECT_TO_SERVER;
import com.message.protocol.PBSystem.SC_USER_RECONNECTION_FAIL;
import com.message.protocol.PBSystem.SC_USER_RECONNECTION_SUCCESS;
import com.message.thrift.actor.ActorMessage;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.japi.pf.ReceiveBuilder;

/**
 * 网络会话传输actor封装 在网络会话和actor中作为桥接.
 *
 * @author ZERO
 */
public class SessionActor extends AbstractActor {

	private static Logger logger = LoggerFactory.getLogger("TCPTransportActor");

	private ActorRef userActor;
	// 关联我的网络会话
	private final IoSession ioSession;
	// 是否连接进入引擎
	private boolean authorization;

	public SessionActor(IoSession ioSession) {
		super();
		this.ioSession = ioSession;
	}

	private void processConnectionSessionsBinding() {
		this.userActor = getSender();
		this.authorization = true;
	}

	/**
	 * 发送网络消息
	 * 
	 * @param requestId
	 *            请求id
	 * @param raw
	 *            数据源
	 */
	private void sessionSendMessage(int requestId, byte[] raw) {
		BaseByteArrayPacket messagePackage = new BaseByteArrayPacket(requestId, raw);
		ioSession.write(messagePackage);
	}

	/**
	 * 处理收到的网络消息
	 * 
	 * @param message
	 */
	private void processIOSessionReciveMessage(BaseByteArrayPacket message) {
		// 检查授权
		if (authorization) {
			ReciveIosessionMessage connect2Server = new ReciveIosessionMessage(message);
			userActor.tell(connect2Server, getSelf());
		} else {
			// 发送给world
			// ActorSelection[Anchor(akka://AVALON/user/NettyIoSession1#883410430),
			// Path(/AvalonWorld)]
			ActorSelection actorSelection = getContext().actorSelection("/user/" + WorldActor.IDENTIFY);
			if (message.getOpCode() == SystemMessageType.USER_LOGIN.id) {
				try {
					CS_USER_CONNECT_TO_SERVER login = CS_USER_CONNECT_TO_SERVER.parseFrom(message.getRawData());
					WorldActor.UserConnect2Server connect2Server = new WorldActor.UserConnect2Server(login.getName(),
							login.getParams());
					actorSelection.tell(connect2Server, getSelf());
				} catch (InvalidProtocolBufferException e) {
					e.printStackTrace();
				}

			} else if (message.getOpCode() == SystemMessageType.USER_RECONNECTION_TRY.id) {
				// TODO 协议
				UserReconnectionTry connect2Server = new UserReconnectionTry("");
				actorSelection.tell(connect2Server, getSelf());
			}

			logger.debug("LocalTransportActor no bindingConnectionSession onReceive msg");
		}
	}

	@Override
	public void postStop() throws Exception {
		super.postStop();
		// 如果有授权，就要通知授权网络断开
		if (authorization) {
			Disconnect disconnect = new Disconnect();
			userActor.tell(disconnect, getSelf());
		}
	}

	@Override
	public Receive createReceive() {
		return ReceiveBuilder.create().match(byte[].class, msg -> {
			ActorMessage actorMessage = ThriftSerializerFactory.getActorMessage(msg);
			switch (actorMessage.op) {
			case SESSION_USER_LOGOUT:

				break;
			case SESSION_USER_CONNECT_TO_SERVER:

				break;
			case SESSION_ENTER_WORLD:
				
				break;
			case SESSION_REBIND_USER:
				
				break;
			case SESSION_RECIVE_IOMESSAGE:
				
				break;
			case SESSION_USERSEND_MESSAGE:
				
				break;
			default:
				break;
			}

		}).match(UserConnect2Server.class, msg -> {
			if (msg.success) {
				SC_USER_RECONNECTION_SUCCESS success = SC_USER_RECONNECTION_SUCCESS.newBuilder().build();
				sessionSendMessage(Message.PB.SystemKey.SC_USER_CONNECT_TO_SERVER_SUCCESS_VALUE, success.toByteArray());
				processConnectionSessionsBinding();
			} else {
				SC_USER_RECONNECTION_FAIL fail = SC_USER_RECONNECTION_FAIL.newBuilder().build();
				sessionSendMessage(Message.PB.SystemKey.SC_USER_RECONNECTION_FAIL_VALUE, fail.toByteArray());
				ioSession.close(true);
			}
		}).match(UserSendMessage2Net.class, msg -> {
			sessionSendMessage(msg.requestId, msg.value);
		}).match(IosessionReciveMessage.class, msg -> {
			processIOSessionReciveMessage(msg.message);
		}).match(ReBindUser.class, msg -> {
			if (msg.findUser) {
				processConnectionSessionsBinding();
			} else {
				ioSession.close(true);
			}
		}).match(UserActor.GetIUser.class, msg -> {
			userActor.tell(msg, getSender());
		}).matchAny(o -> logger.info("received unknown message")).build();
	}

	public static class UserLogout {
	}

	public static class UserConnect2Server {
		public final boolean success;

		public UserConnect2Server(boolean success) {
			super();
			this.success = success;
		}

	}

	public static class EnterWorld {
		public final IUser user;

		public EnterWorld(IUser user) {
			super();
			this.user = user;
		}
	}

	public static class UserSendMessage2Net {
		public final int requestId;
		public final byte[] value;

		public UserSendMessage2Net(int requestId, byte[] value) {
			super();
			this.requestId = requestId;
			this.value = value;
		}

	}

	public static class IosessionReciveMessage {
		public final BaseByteArrayPacket message;

		public IosessionReciveMessage(BaseByteArrayPacket message) {
			super();
			this.message = message;
		}
	}

	public static class ReBindUser {
		public final boolean findUser;

		public ReBindUser(boolean findUser) {
			super();
			this.findUser = findUser;
		}

	}
}
