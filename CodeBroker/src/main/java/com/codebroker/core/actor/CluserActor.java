package com.codebroker.core.actor;

import akka.actor.*;
import akka.japi.pf.ReceiveBuilder;
import com.codebroker.api.event.Event;
import com.codebroker.core.ContextResolver;
import com.codebroker.core.ServerEngine;
import com.codebroker.core.actor.ServerCluserActorProxy.State;
import com.codebroker.core.data.IObject;
import com.codebroker.protocol.ThriftSerializerFactory;
import com.codebroker.util.AkkaMediator;
import com.google.common.collect.Queues;
import com.message.thrift.actor.ActorMessage;
import com.message.thrift.actor.Operation;
import com.message.thrift.actor.cluser.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * 集群的Actor事件处理
 *
 * @author zero
 */
public class CluserActor extends AbstractActor {

    /**
     * 1560657262
     */
    public long uid;
    /**
     * 192.168.0.127
     */
    public String host;
    /**
     * CodeBroker@192.168.0.127:2551
     */
    public String hostPort;
    /**
     * 2251
     */
    public Integer port;
    /**
     * CodeBroker
     */
    public String system;
    /**
     * akka.tcp
     */
    public String protocol;
    ThriftSerializerFactory thriftSerializerFactory = new ThriftSerializerFactory();
    private Logger logger = LoggerFactory.getLogger(getClass());
    private List<ServerCluserActorProxy> list = new ArrayList<ServerCluserActorProxy>();

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create().match(byte[].class, msg -> {
            ActorMessage actorMessage = thriftSerializerFactory.getActorMessage(msg);
            switch (actorMessage.op) {
                case CLUSER_INIT:
                    CluserInitMessage initMessage = new CluserInitMessage();
                    thriftSerializerFactory.deserialize(initMessage, actorMessage.messageRaw);
                    cluserRegedit(initMessage);
                    break;
                case CLUSER_HELLO:
                    CluserHelloMessage cluserHelloMessage = new CluserHelloMessage();
                    thriftSerializerFactory.deserialize(cluserHelloMessage, actorMessage.getMessageRaw());
                    handshake(cluserHelloMessage);
                    break;
                case CLUSER_SEND:
                    CluserSendMessage cluserSendMessage = new CluserSendMessage();
                    thriftSerializerFactory.deserialize(cluserSendMessage, actorMessage.getMessageRaw());
                    sendMessage(cluserSendMessage);
                    break;
                case CLUSER_RECIVE:
                    CluserReciveMessage cluserReciveMessage = new CluserReciveMessage();
                    thriftSerializerFactory.deserialize(cluserReciveMessage, actorMessage.getMessageRaw());
                    reciveCluserMessage(cluserReciveMessage);
                    break;

                default:
                    logger.info("unknow message " + msg);
                    break;
            }
        }).match(IObject.class, msg -> {
            System.out.println("woddeede " + msg.getUtfString("hello"));
        }).match(Event.class, msg -> {
            System.out.println(msg.getTopic());
            System.out.println(msg.getMessage().toJson());
        })
                .matchAny(msg -> {
                    logger.info("unknow message " + msg);
                }).build();
    }

    private void reciveCluserMessage(CluserReciveMessage msg) {
        ActorSelection systemActorSelection = AkkaMediator.getSystemActorSelection(msg.actorPath);
        byte[] tbaseMessage = thriftSerializerFactory.getActorMessageByteArray(Operation.CLUSER_RECIVE, msg);
        systemActorSelection.tell(tbaseMessage, getSender());
    }

    private void sendMessage(CluserSendMessage msg) {
        for (ServerCluserActorProxy serverCluserActorProxy : list) {
            if (serverCluserActorProxy.getServerId() == msg.serverId) {
                // 目标Actor Path,命令,命令元数据
                CluserReciveMessage message = new CluserReciveMessage(msg.actorPath, msg.cmd, msg.messageRaw);
                byte[] tbaseMessage = thriftSerializerFactory.getActorMessageByteArray(Operation.CLUSER_RECIVE, message);
                serverCluserActorProxy.sendMessage(tbaseMessage, getSelf());
            }
        }
    }

    /**
     * 处理握手函数
     *
     * @param msg
     */
    private void handshake(CluserHelloMessage msg) {
        if (msg.state.equals(Handshake.SEND)) {
            // **收到握手信息
            for (ServerCluserActorProxy serverCluserActorProxy : list) {
                // 找到对应的服务器，设置服务器ID
                if (serverCluserActorProxy.uid == msg.uid) {
                    serverCluserActorProxy.setActorRef(getSender());
                    if (serverCluserActorProxy.getState().equals(State.LOST)) {
                        serverCluserActorProxy.sendHasMessage(getSelf());
                        serverCluserActorProxy.setState(State.READY);

                    } else {
                        serverCluserActorProxy.setServerId(msg.serverId);
                    }
                    CluserHelloMessage cluserHelloMessage = new CluserHelloMessage(ServerEngine.serverId, uid, Handshake.BACK);
                    byte[] tbaseMessage = thriftSerializerFactory.getActorMessageByteArray(Operation.CLUSER_HELLO, cluserHelloMessage);
                    getSender().tell(tbaseMessage, getSelf());

                }
            }
            System.out.println("now server is " + list.size());
        } else if (msg.state.equals(Handshake.BACK)) {
            boolean has = false;
            for (ServerCluserActorProxy serverCluserActorProxy : list) {
                // 找到对应的服务器，设置服务器ID
                if (serverCluserActorProxy.uid == msg.uid) {
                    serverCluserActorProxy.setActorRef(getSender());
                    serverCluserActorProxy.setState(State.READY);
                    has = true;
                    System.out.println("now server is " + list.size());
                }
            }
            if (!has) {
                ServerCluserActorProxy serverCluserActorProxy = new ServerCluserActorProxy(msg.uid, msg.serverId);
                list.add(serverCluserActorProxy);
                System.out.println("now server is " + list.size());
            }
        } else {

        }
    }

    /**
     * 注册一个集群节点
     *
     * @param msg
     */
    private void cluserRegedit(CluserInitMessage msg) {
        // 如果是本机的话
        if (getSender().equals(getSelf())) {
            logger.info("Get self Node uid=" + uid);
            this.uid = msg.longUid;
            this.host = msg.host;
            this.hostPort = msg.hostPort;
            this.port = msg.port;
            this.system = msg.system;
            this.protocol = msg.protocol;
        } else {
            ServerCluserActorProxy cluserActorProxy;
            boolean has = false;
            for (ServerCluserActorProxy serverCluserActorProxy : list) {
                if (serverCluserActorProxy.uid == msg.longUid) {
                    logger.info("Get allready Node uid=" + uid);
                    cluserActorProxy = serverCluserActorProxy;
                    cluserActorProxy.host = msg.host;
                    cluserActorProxy.hostPort = msg.hostPort;
                    cluserActorProxy.port = msg.port;
                    cluserActorProxy.system = msg.system;
                    cluserActorProxy.protocol = msg.protocol;
                    has = true;
                }
            }
            if (!has) {
                logger.info("Get not ready Node uid=" + uid);
                cluserActorProxy = new ServerCluserActorProxy(msg.longUid, msg.host, msg.hostPort, msg.port, msg.system,
                        msg.protocol);
                list.add(cluserActorProxy);
            }
            // 不是本机的处理

            CluserHelloMessage cluserHelloMessage = new CluserHelloMessage(ServerEngine.serverId, uid, Handshake.SEND);
            byte[] tbaseMessage = thriftSerializerFactory.getActorMessageByteArray(Operation.CLUSER_HELLO, cluserHelloMessage);

            Address addr = new Address(msg.protocol, msg.system, msg.host, msg.port);
            ActorSystem actorSystem = ContextResolver.getActorSystem();
            // 跟对方服务器握手
            ActorSelection actorSelection = actorSystem.actorSelection(addr + "/user/ClusterListener/CluserActor");
            actorSelection.tell(tbaseMessage, getSelf());
        }
    }

}

/**
 * 代理服务器对象
 *
 * @author zero
 */
class ServerCluserActorProxy {
    /**
     * 1560657262
     */
    public long uid;
    /**
     * 192.168.0.127
     */
    public String host;
    /**
     * CodeBroker@192.168.0.127:2551
     */
    public String hostPort;
    /**
     * 2251
     */
    public Integer port;

    /**
     * CodeBroker
     */
    public String system;
    /**
     * akka.tcp
     */
    public String protocol;

    private int serverId;
    private ActorRef actorRef;
    private State state;
    private Queue<byte[]> waitSend = Queues.newLinkedBlockingDeque();

    public ServerCluserActorProxy(long uid, String host, String hostPort, Integer port, String system,
                                  String protocol) {
        super();
        this.uid = uid;
        this.host = host;
        this.hostPort = hostPort;
        this.port = port;
        this.system = system;
        this.protocol = protocol;
        this.state = State.WAIT;
    }

    public ServerCluserActorProxy(long uid, int serverId) {
        super();
        this.uid = uid;
        this.serverId = serverId;
        this.state = State.WAIT;
    }

    public void sendHasMessage(ActorRef self) {
        while (!waitSend.isEmpty()) {
            byte[] poll = waitSend.poll();
            actorRef.tell(poll, self);
        }

    }

    public void sendMessage(byte[] message, ActorRef sender) {
        if (state.equals(State.READY)) {
            actorRef.tell(message, sender);
        } else {
            waitSend.add(message);
        }
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public ActorRef getActorRef() {
        return actorRef;
    }

    public void setActorRef(ActorRef actorRef) {
        this.actorRef = actorRef;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    enum State {
        WAIT, READY, LOST;
    }

}
