package com.codebroker.core.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.PoisonPill;
import akka.japi.pf.ReceiveBuilder;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.codebroker.api.IoSession;
import com.codebroker.api.internal.ByteArrayPacket;
import com.codebroker.core.data.IObject;
import com.codebroker.net.TransportType;
import com.codebroker.protocol.BaseByteArrayPacket;
import com.codebroker.protocol.SystemProtocolCodecFactory;
import com.codebroker.protocol.ThriftSerializerFactory;
import com.codebroker.setting.SystemRequest;
import com.message.protocol.Message;
import com.message.protocol.PBSystem;
import com.message.thrift.actor.ActorMessage;
import com.message.thrift.actor.Operation;
import com.message.thrift.actor.session.UserConnect2Server;
import com.message.thrift.actor.user.ReciveIosessionMessage;
import com.message.thrift.actor.world.UserConnect2World;
import com.message.thrift.actor.world.UserReconnectionTry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import com.message.protocol.PBSystem.CS_USER_CONNECT_TO_SERVER;
//import com.message.protocol.PBSystem.SC_USER_RECONNECTION_FAIL;
//import com.message.protocol.PBSystem.SC_USER_RECONNECTION_SUCCESS;

/**
 * 网络会话传输actor封装 在网络会话和actor中作为桥接.
 *
 * @author ZERO
 */
public class SessionActor extends AbstractActor {

    private static Logger logger = LoggerFactory.getLogger("TCPTransportActor");
    // 关联我的网络会话
    private final IoSession ioSession;
    ThriftSerializerFactory thriftSerializerFactory = new ThriftSerializerFactory();
    private SystemProtocolCodecFactory protocolCodec = new SystemProtocolCodecFactory();
    /**
     * 网络传输协议类型
     */
    private TransportType transportType = TransportType.UNKNOW;
    private ActorRef userActor;
    // 是否连接进入引擎
    private boolean authorization;

    public SessionActor(IoSession ioSession) {
        super();
        this.ioSession = ioSession;
    }

    private void processConnectionSessionsBinding(String bindingkey) {
        this.userActor = getSender();
        this.authorization = true;
    }

    /**
     * 发送网络消息
     *
     * @param requestId 请求id
     * @param raw       数据源
     */
    private void sessionWriteMessage(int requestId, byte[] raw) {
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
            ReciveIosessionMessage connect2Server = new ReciveIosessionMessage(message.getOpCode(), message.toByteBuffer());
            byte[] actorMessageWithSubClass = thriftSerializerFactory.getActorMessageByteArray(Operation.USER_RECIVE_IOSESSION_MESSAGE, connect2Server);
            userActor.tell(actorMessageWithSubClass, getSelf());
        } else {


            // 发送给world
            // ActorSelection[Anchor(akka://AVALON/user/NettyIoSession1#883410430),
            // Path(/AvalonWorld)]
            ActorSelection actorSelection = getContext().actorSelection("/user/" + WorldActor.IDENTIFY);
            /**
             * 处理玩家登入
             */
            if (message.getOpCode() == SystemRequest.USER_LOGIN_PB.id) {
                transportType = TransportType.PROTOBUFF;
                IObject iObject = protocolCodec.unpackByteArrayPacket(message);
                UserConnect2World userConnect2World = new UserConnect2World(iObject.getUtfString("name"), iObject.getUtfString("params"));
                byte[] actorMessageWithSubClass = thriftSerializerFactory.getActorMessageByteArray(Operation.WORLD_USER_CONNECT_2_WORLD, userConnect2World);
                actorSelection.tell(actorMessageWithSubClass, getSelf());

            } else if (message.getOpCode() == SystemRequest.USER_LOGIN_JSON.id) {
                transportType = TransportType.JSON;
                String para = new String(message.getRawData());
                JSONObject parseObject = JSON.parseObject(para);

                UserConnect2World userConnect2World = new UserConnect2World(parseObject.getString("name"),
                        parseObject.getString("parm"));
                byte[] actorMessageWithSubClass = thriftSerializerFactory
                        .getActorMessageByteArray(Operation.WORLD_USER_CONNECT_2_WORLD, userConnect2World);
                actorSelection.tell(actorMessageWithSubClass, getSelf());
            }
            /**
             * 处理用户重新连接
             */
            else if (message.getOpCode() == SystemRequest.USER_RECONNECTION_TRY.id) {
                // TODO 协议
                UserReconnectionTry reconnectionTry = new UserReconnectionTry("");
                byte[] actorMessageWithSubClass = thriftSerializerFactory
                        .getActorMessageByteArray(Operation.WORLD_USER_RECONNECTION_TRY, reconnectionTry);

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
            byte[] tbaseMessage = thriftSerializerFactory.getOnlySerializerByteArray(Operation.USER_DISCONNECT);
            userActor.tell(tbaseMessage, getSelf());
        }
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(byte[].class, msg ->
                {
                    ActorMessage actorMessage = thriftSerializerFactory.getActorMessage(msg);
                    switch (actorMessage.op) {
                        /**
                         * 关闭网络回话
                         */
                        case SESSION_USER_LOGOUT:
                            ioSession.close(true);
                            // 关闭自己
                            self().tell(PoisonPill.getInstance(), getSelf());
                            break;
                        case SESSION_USER_CONNECT_TO_SERVER:
                            UserConnect2Server connect2Server = new UserConnect2Server();
                            thriftSerializerFactory.deserialize(connect2Server, actorMessage.messageRaw);

                            if (connect2Server.success) {
                                if (transportType.equals(TransportType.PROTOBUFF)) {
                                    PBSystem.SC_USER_RECONNECTION_SUCCESS success = PBSystem.SC_USER_RECONNECTION_SUCCESS.newBuilder().build();
                                    sessionWriteMessage(Message.PB.SystemKey.SC_USER_CONNECT_TO_SERVER_SUCCESS_VALUE, success.toByteArray());
                                    processConnectionSessionsBinding(connect2Server.bindingkey);
                                } else if (transportType.equals(TransportType.JSON)) {
                                    processConnectionSessionsBinding(connect2Server.bindingkey);
                                    JSONObject object = new JSONObject();
                                    object.put("connection", "ok");
                                    object.put("bindingkey", connect2Server.bindingkey);
                                    sessionWriteMessage(SystemRequest.USER_LOGIN_JSON.id, object.toString().getBytes());
                                }
                            } else {
                                if (transportType.equals(TransportType.PROTOBUFF)) {
                                    PBSystem.SC_USER_RECONNECTION_FAIL fail = PBSystem.SC_USER_RECONNECTION_FAIL.newBuilder().build();
                                    sessionWriteMessage(Message.PB.SystemKey.SC_USER_RECONNECTION_FAIL_VALUE, fail.toByteArray());
                                    ioSession.close(true);
                                } else if (transportType.equals(TransportType.JSON)) {
                                    ioSession.close(true);
                                }
                            }
                            break;
                        case SESSION_REBIND_USER:
                            com.message.thrift.actor.session.ReBindUser reBindUser = new com.message.thrift.actor.session.ReBindUser();
                            thriftSerializerFactory.deserialize(reBindUser, actorMessage.messageRaw);
                            if (reBindUser.success) {
                                processConnectionSessionsBinding(reBindUser.bindingkey);
                            } else {
                                ioSession.close(true);
                            }
                            break;
                        case SESSION_RECIVE_PACKET:
                            ByteArrayPacket baseByteArrayPacket = new BaseByteArrayPacket();
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

                }).matchAny(o -> logger.info("received unknown message")).build();
    }

}
