package com.codebroker.core.actor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codebroker.api.IoSession;
import com.codebroker.api.internal.ByteArrayPacket;
import com.codebroker.protocol.BaseByteArrayPacket;
import com.codebroker.protocol.ThriftSerializerFactory;
import com.codebroker.setting.SystemMessageType;
import com.google.protobuf.InvalidProtocolBufferException;
import com.message.protocol.Message;
import com.message.protocol.PBSystem.CS_USER_CONNECT_TO_SERVER;
import com.message.protocol.PBSystem.SC_USER_RECONNECTION_FAIL;
import com.message.protocol.PBSystem.SC_USER_RECONNECTION_SUCCESS;
import com.message.thrift.actor.ActorMessage;
import com.message.thrift.actor.Operation;
import com.message.thrift.actor.session.UserConnect2Server;
import com.message.thrift.actor.user.ReciveIosessionMessage;
import com.message.thrift.actor.world.UserConnect2World;
import com.message.thrift.actor.world.UserReconnectionTry;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.PoisonPill;
import akka.japi.pf.ReceiveBuilder;

/**
 * 网络会话传输actor封装 在网络会话和actor中作为桥接.
 *
 * @author ZERO
 */
public class SessionActor extends AbstractActor {

	private static Logger logger = LoggerFactory.getLogger("TCPTransportActor");
	ThriftSerializerFactory thriftSerializerFactory=new ThriftSerializerFactory();
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
	private void processIOSessionReciveMessage(ByteArrayPacket message) {
		// 检查授权
		if (authorization) {
			ReciveIosessionMessage connect2Server = new ReciveIosessionMessage(message.getOpCode(),message.toByteBuffer());
			byte[] actorMessageWithSubClass = thriftSerializerFactory.getActorMessageWithSubClass(Operation.USER_RECIVE_IOSESSION_MESSAGE, connect2Server);
			userActor.tell(actorMessageWithSubClass, getSelf());
		} else {
			// 发送给world
			// ActorSelection[Anchor(akka://AVALON/user/NettyIoSession1#883410430),
			// Path(/AvalonWorld)]
			ActorSelection actorSelection = getContext().actorSelection("/user/" + WorldActor.IDENTIFY);
			/**
			 * 处理玩家登入
			 */
			if (message.getOpCode() == SystemMessageType.USER_LOGIN.id) {
				try {
					CS_USER_CONNECT_TO_SERVER login = CS_USER_CONNECT_TO_SERVER.parseFrom(message.getRawData());
					
					UserConnect2World userConnect2World=new UserConnect2World(login.getName(),login.getParams());
					byte[] actorMessageWithSubClass = thriftSerializerFactory.getActorMessageWithSubClass(Operation.WORLD_USER_CONNECT_2_WORLD, userConnect2World);
					actorSelection.tell(actorMessageWithSubClass, getSelf());
					
				} catch (InvalidProtocolBufferException e) {
					e.printStackTrace();
				}

			}
			/**
			 * 处理用户重新连接
			*/ 
			else if (message.getOpCode() == SystemMessageType.USER_RECONNECTION_TRY.id) {
				// TODO 协议
				UserReconnectionTry reconnectionTry = new UserReconnectionTry("");
				byte[] actorMessageWithSubClass = 
						thriftSerializerFactory.getActorMessageWithSubClass(Operation.WORLD_USER_RECONNECTION_TRY, reconnectionTry);
				
				actorSelection.tell(actorMessageWithSubClass, getSelf());
			}

			logger.debug("LocalTransportActor no bindingConnectionSession onReceive msg");
		}
	}

	@Override
	public void postStop() throws Exception {
		super.postStop();
		// 如果有授权，就要通知授权网络断开
		if (authorization) {
			byte[] tbaseMessage = thriftSerializerFactory.getTbaseMessage(Operation.USER_DISCONNECT);
			userActor.tell(tbaseMessage, getSelf());
		}
	}

	@Override
	public Receive createReceive() {
		return ReceiveBuilder
			.create()
			.match(byte[].class, msg -> {
			ActorMessage actorMessage = thriftSerializerFactory.getActorMessage(msg);
			switch (actorMessage.op) {
			/**
			 * 关闭网络回话
			 */
			case SESSION_USER_LOGOUT:
				ioSession.close(true);
				//关闭自己
				self().tell(PoisonPill.getInstance(), getSelf());
				break;
			case SESSION_USER_CONNECT_TO_SERVER:
				UserConnect2Server connect2Server=new UserConnect2Server();
				thriftSerializerFactory.deserialize(connect2Server, actorMessage.messageRaw);
					if (connect2Server.success) {
						SC_USER_RECONNECTION_SUCCESS success = SC_USER_RECONNECTION_SUCCESS.newBuilder().build();
						sessionSendMessage(Message.PB.SystemKey.SC_USER_CONNECT_TO_SERVER_SUCCESS_VALUE, success.toByteArray());
						processConnectionSessionsBinding();
					} else {
						SC_USER_RECONNECTION_FAIL fail = SC_USER_RECONNECTION_FAIL.newBuilder().build();
						sessionSendMessage(Message.PB.SystemKey.SC_USER_RECONNECTION_FAIL_VALUE, fail.toByteArray());
						ioSession.close(true);
					}
				break;
			case SESSION_REBIND_USER:
				com.message.thrift.actor.session.ReBindUser reBindUser=new com.message.thrift.actor.session.ReBindUser();
				thriftSerializerFactory.deserialize(reBindUser, actorMessage.messageRaw);
				if (reBindUser.success) {
					processConnectionSessionsBinding();
				} else {
					ioSession.close(true);
				}
				break;
			case SESSION_RECIVE_PACKET:
				ByteArrayPacket baseByteArrayPacket=new BaseByteArrayPacket(); 
				baseByteArrayPacket.fromBuffer(actorMessage.messageRaw);
				processIOSessionReciveMessage(baseByteArrayPacket);
				break;
			case SESSION_USER_SEND_PACKET:
				ByteArrayPacket messagePackage = new BaseByteArrayPacket();
				messagePackage.fromBuffer(actorMessage.messageRaw);
				ioSession.write(messagePackage);
				break;
			case USER_GET_IUSER:
				userActor.tell(msg, getSender());
				break;
			default:
				break;
			}

		}).matchAny(o -> logger.info("received unknown message"))
		  .build();
	}



}
