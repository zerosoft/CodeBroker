package com.codebroker.core.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.codebroker.api.CodeBrokerAppListener;
import com.codebroker.api.internal.ByteArrayPacket;
import com.codebroker.core.ContextResolver;
import com.codebroker.core.ServerEngine;
import com.codebroker.core.cluster.ClusterDistributedPub;
import com.codebroker.core.local.WorldCreateNPC;
import com.codebroker.core.manager.CacheManager;
import com.codebroker.exception.AllReadyRegeditException;
import com.codebroker.exception.NoAuthException;
import com.codebroker.protocol.BaseByteArrayPacket;
import com.codebroker.protocol.ThriftSerializerFactory;
import com.codebroker.util.AkkaUtil;
import com.codebroker.util.LogUtil;
import com.message.thrift.actor.ActorMessage;
import com.message.thrift.actor.Operation;
import com.message.thrift.actor.areamanager.CreateArea;
import com.message.thrift.actor.usermanager.CreateUserWithSession;
import com.message.thrift.actor.world.*;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 顶级游戏世界 一个JVM仅有此一个 下级 {@code IArea} 管理器 {@code IAreaManager} 下级{@code IGrid}}
 * 管理器{@code IArea}
 *
 * @author server
 */
public class WorldActor extends AbstractActor {


    public static final String IDENTIFY = WorldActor.class.getSimpleName().toString();
    private static Logger logger = LoggerFactory.getLogger("WorldActor");
    ThriftSerializerFactory thriftSerializerFactory = new ThriftSerializerFactory();
    Map<String, ActorRef> gmSession = new ConcurrentHashMap<String, ActorRef>();
    /**
     * 保存服务器WorldActor地址的
     * akka.tcp://CodeBroker@192.168.0.127:2551/user/WorldActor
     */
    private Map<Integer, String> serverIdMap = new HashMap<Integer, String>();
    //区域管理器的地址引用
    private ActorRef areaManagerRef;
    //用户管理器的地址引用
    private ActorRef userManagerRef;

    //NPC用户管理器的地址引用
    private ActorRef npcManagerRef;

    @Override
    public void preStart() throws Exception {
        super.preStart();
        initialize();
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(byte[].class, msg -> {
                    ActorMessage actorMessage = thriftSerializerFactory.getActorMessage(msg);
                    switch (actorMessage.op) {
                        case WORLD_USER_CONNECT_2_WORLD:
                            UserConnect2World userConnect2World = new UserConnect2World();
                            thriftSerializerFactory.deserialize(userConnect2World, actorMessage.messageRaw);
                            userConnect2Server(userConnect2World.name, userConnect2World.params, getSender());
                            break;
                        case WORLD_USER_REGEDIT_2_WORLD:
                            UserRegedit2World userRegedit2World = new UserRegedit2World();
                            thriftSerializerFactory.deserialize(userRegedit2World, actorMessage.messageRaw);
                            userRegedit2Server(userRegedit2World.name, userRegedit2World.params, getSender());
                            break;
                        case WORLD_USER_RECONNECTION_TRY:
                            UserReconnectionTry reconnectionTry = new UserReconnectionTry();
                            thriftSerializerFactory.deserialize(reconnectionTry, actorMessage.messageRaw);
                            processReconnection(reconnectionTry.reBindKey, getSender());
                            break;
                        case AREA_MANAGER_CREATE_AREA:
                            CreateArea createArea = new CreateArea();
                            byte[] actorMessageWithSubClass = thriftSerializerFactory
                                    .getActorMessageByteArray(Operation.AREA_MANAGER_CREATE_AREA, createArea);
                            areaManagerRef.tell(actorMessageWithSubClass, getSender());
                            break;
                        case WORLD_NER_SERVER_COMING:
                            NewServerComeIn newServerComeIn = new NewServerComeIn();
                            thriftSerializerFactory.deserialize(newServerComeIn, actorMessage.messageRaw);
                            processNewCome(newServerComeIn.serverUId, newServerComeIn.remotePath);
                            break;
                        case WORLD_HAND_SHAKE:
                            HandShake handShake = new HandShake();
                            thriftSerializerFactory.deserialize(handShake, actorMessage.messageRaw);
                            processShank(handShake.serverId, handShake.serverUid);
                            break;
                        default:
                            break;
                    }
                })
                //转发给用户管理器
                .match(WorldCreateNPC.class, msg -> {
                    userManagerRef.tell(msg, getSelf());
                })

                .match(String.class, msg -> {
                    processStringMessage(msg);
                }).match(HelloWorld.class, msg -> {
                    System.err.println(getSender().path().toString());
                })
                .matchAny(o -> logger.info("received unknown message {}", o))
                .build();
    }

    private void userRegedit2Server(String name, String params, ActorRef sessionActorRef) {
        // 验证登入
        CodeBrokerAppListener appListener = ContextResolver.getAppListener();
        boolean handleLogin;
        if (appListener != null) {
            try {
                handleLogin = appListener.handleRegedit(name, params);
            } catch (AllReadyRegeditException exp) {
                handleLogin = false;
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("result", handleLogin);
                sendMessageToIoSession(8, jsonObject.toString().getBytes(), sessionActorRef);
                return;
            }
        } else {
            handleLogin = false;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("result", handleLogin);
        sendMessageToIoSession(8, jsonObject.toString().getBytes(), sessionActorRef);
    }

    private void initialize() {
        logger.debug("initialize Game word need start");
        CacheManager component = ContextResolver.getComponent(CacheManager.class);
        /**
         * 用户管理器 akka://CodeBroker/user/WorldActor/UserManagerActor
         */
        userManagerRef = getContext().actorOf(Props.create(UserManagerActor.class), UserManagerActor.IDENTIFY);
        getContext().watch(userManagerRef);

        component.putActorGlobalPath(UserManagerActor.IDENTIFY, userManagerRef);
        logger.info("UserManager Path= {}", userManagerRef.path().toString());
        /**
         * 用户管理器 akka://CodeBroker/user/WorldActor/NPCManagerActor
         */
        npcManagerRef = getContext().actorOf(Props.create(NPCManagerActor.class), NPCManagerActor.IDENTIFY);
        getContext().watch(npcManagerRef);

        component.putActorGlobalPath(NPCManagerActor.IDENTIFY, npcManagerRef);
        logger.info("NPCManager Path= {}", userManagerRef.path().toString());

        /**
         * 初始化空间管理器 akka://CodeBroker/user/WorldActor/AreaManagerActor
         */
        areaManagerRef = getContext().actorOf(Props.create(AreaManagerActor.class), AreaManagerActor.IDENTIFY);
        component.putActorGlobalPath(AreaManagerActor.IDENTIFY, areaManagerRef);

        getContext().watch(areaManagerRef);
        logger.info("AreaManager Path= {}", areaManagerRef.path().toString());
    }


    private void processShank(int serverId, long serverUid) {
        // akka.tcp://CodeBroker@192.168.0.202:2551/
        if (serverId != ServerEngine.serverId) {
            //TODO
        }
        serverIdMap.put(serverId, getSender().path().parent().toString());
        // akka.tcp://CodeBroker@192.168.0.127:2551/user/WorldActor
        logger.info("get HandShaske {} path {} ", serverId, getSender().path().parent().toString());
    }

    /**
     * 处理用户从新连接
     *
     * @param actorRef
     */
    private void processReconnection(String key, ActorRef actorRef) {
        UserManagerActor.FindUserByRebindKey message = new UserManagerActor.FindUserByRebindKey(key);
        userManagerRef.tell(message, actorRef);
    }

    private void userConnect2Server(String name, String parms, ActorRef sessionActorRef) {
        // 验证登入
        CodeBrokerAppListener appListener = ContextResolver.getAppListener();
        String handleLogin;
        if (appListener != null) {
            try {
                handleLogin = appListener.handleLogin(name, parms);
            } catch (NoAuthException exc) {
                handleLogin = "Test1234";
            }
        } else {
            handleLogin = "Test1234";
        }
        CreateUserWithSession createUserWithSession = new CreateUserWithSession(handleLogin,name,parms);
        byte[] actorMessageWithSubClass = thriftSerializerFactory.getActorMessageByteArray(Operation.USER_MANAGER_CREATE_USER_WITH_SESSION, createUserWithSession);
        userManagerRef.tell(actorMessageWithSubClass, sessionActorRef);
    }


    private void processNewCome(long serverUId, String remotePath) {
        // msg.remotePath=akka.tcp://CodeBroker@192.168.0.127:25514
        String fixSupervisorPath = AkkaUtil.getFixSupervisorPath(remotePath, WorldActor.IDENTIFY);
        ActorSelection remoteActorSelection = AkkaUtil.getRemoteActorSelection(fixSupervisorPath);

        HandShake handShake = new HandShake(ServerEngine.serverId, serverUId);
        byte[] actorMessageWithSubClass = thriftSerializerFactory.getActorMessageByteArray(Operation.WORLD_HAND_SHAKE, handShake);

        remoteActorSelection.tell(actorMessageWithSubClass, getSelf());
    }

    public void sendMessageToIoSession(int requestId, Object message, ActorRef sessionRef) {

        ActorMessage actorMessage = new ActorMessage();

        ByteArrayPacket byteArrayPacket = new BaseByteArrayPacket(requestId, (byte[]) message);
        actorMessage.messageRaw = byteArrayPacket.toByteBuffer();
        actorMessage.op = Operation.SESSION_USER_SEND_PACKET;
        try {
            byte[] bs = thriftSerializerFactory.getActorMessage(actorMessage);
            sessionRef.tell(bs, getSelf());
        } catch (TException e) {
           LogUtil.exceptionPrint(e);
        }

    }

    private void processStringMessage(String msg) {
        logger.info("get Message {}", msg);
        /**
         * 处理GMsession
         */
        JSONObject json = JSON.parseObject(msg);
        ActorRef actorRef = null;
        String COMMAND_ID = json.getString("id");
        if (gmSession.containsKey(COMMAND_ID)) {
            actorRef = gmSession.get(COMMAND_ID);
        } else {
            gmSession.put(COMMAND_ID, actorRef);
        }

        CacheManager component = ContextResolver.getComponent(CacheManager.class);
        ActorRef localPath = component.getActorGlobalPath(ClusterDistributedPub.IDENTIFY);
        localPath.tell("ssss", ActorRef.noSender());

        actorRef.tell(msg, getSender());
    }


    public static class HelloWorld implements Serializable {

        private static final long serialVersionUID = -3459373230872074079L;

        public final int selfServerId;

        public HelloWorld(int selfServerId) {
            super();
            this.selfServerId = selfServerId;
        }
    }


    public static class RemoveServer {

        public final long serverUId;

        public RemoveServer(long serverUId) {
            super();
            this.serverUId = serverUId;
        }

    }

}
