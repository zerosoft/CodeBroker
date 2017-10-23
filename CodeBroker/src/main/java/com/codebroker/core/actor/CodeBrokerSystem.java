package com.codebroker.core.actor;

import akka.actor.*;
import akka.japi.Creator;
import akka.japi.pf.ReceiveBuilder;
import com.codebroker.core.cluster.ClusterDistributedPub;
import com.codebroker.core.cluster.ClusterDistributedSub;
import com.codebroker.core.cluster.ClusterListener;
import com.codebroker.core.message.CommonMessage;
import com.codebroker.core.model.CodeDeadLetter;
import com.codebroker.core.monitor.MonitorManager;
import com.codebroker.exception.NoInstanceException;
import com.codebroker.protocol.ThriftSerializerFactory;
import com.codebroker.util.LogUtil;
import com.message.thrift.actor.Operation;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 自定义最高级的Actor系统
 *
 * @author ZERO
 */
public class CodeBrokerSystem extends AbstractActor {

    public static final String IDENTIFY = CodeBrokerSystem.class.getSimpleName();
    /**
     * 外部使用的单例
     */
    public static CodeBrokerSystem instance;
    private static Logger logger = LoggerFactory.getLogger("CodeBrokerSystem");
    private final ActorSystem actorSystem;
    ThriftSerializerFactory thriftSerializerFactory = new ThriftSerializerFactory();
    private ActorRef monitorManager;
    private ActorRef clusterListener;
    private ActorRef world;
    private ActorRef elkLogger;
    private ActorRef clusterDistributedPub;
    private ActorRef clusterDistributedSub;
    private ActorRef deadLetterRef;

    public CodeBrokerSystem(ActorSystem actorSystem) {
        super();
        this.actorSystem = actorSystem;
        CodeBrokerSystem.instance = this;
    }

    public static CodeBrokerSystem getInstance() {
        if (instance == null)
            throw new NoInstanceException();
        return instance;
    }

    public static Props props(ActorSystem actorSystem) {
        Props create = Props.create(new selfCreator(actorSystem));
        create.withDispatcher("session-default-dispatcher");
        return create;
    }

    private void processStart() {
        /**
         * 集群监听
         */
        clusterListener =
                actorSystem.actorOf(Props.create(ClusterListener.class), ClusterListener.IDENTIFY);
        this.getContext().watch(clusterListener);

        /**
         * 错误地址信息
         */
        Props avalonDeadLetterProps = Props.create(CodeDeadLetter.class);
        deadLetterRef = actorSystem.actorOf(avalonDeadLetterProps);
        actorSystem.eventStream().subscribe(deadLetterRef, DeadLetter.class);
        /**
         * 初始化游戏世界
         */
        world = actorSystem.actorOf(Props.create(WorldActor.class), WorldActor.IDENTIFY);
        this.getContext().watch(world);
        //初始化WORLD
        try {
            byte[] tbaseMessage = thriftSerializerFactory.getOnlySerializerByteArray(Operation.WORLD_INITIALIZE);
            world.tell(tbaseMessage, getSelf());
        } catch (TException e) {
            e.printStackTrace();
        }

        logger.info("World Path=" + world.path().toString());
        /**
         * ELK日志记录
         */
        elkLogger = actorSystem.actorOf(Props.create(ELKLogActor.class), ELKLogActor.IDENTIFY);
        this.getContext().watch(elkLogger);
        LogUtil.elkLog = elkLogger;
        logger.info("ELKActor Path=" + elkLogger.path().toString());
        /**
         * 分布式发布Actor
         */
        Props pub = Props.create(ClusterDistributedPub.class);
        clusterDistributedPub =
                actorSystem.actorOf(pub, ClusterDistributedPub.IDENTIFY);
        this.getContext().watch(clusterDistributedPub);
        /**
         * 分布式订阅 Actor
         */
        Props sub = Props.create(ClusterDistributedSub.class, "CODE_BORKER_TOPIC");
        clusterDistributedSub =
                actorSystem.actorOf(sub, ClusterDistributedSub.IDENTIFY);
        this.getContext().watch(clusterDistributedSub);
        /**
         * 数据相关监听Actor
         */
        Props create = Props.create(MonitorManager.class);
        monitorManager = actorSystem.actorOf(create, MonitorManager.IDENTIFY);
        this.getContext().watch(monitorManager);
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(CommonMessage.Start.class, msg -> {
                    processStart();
                }).match(CommonMessage.Restart.class, msg -> {
                    processRestart();
                }).match(CommonMessage.Close.class, msg -> {
                    processStop();
                }).build();
    }

    private void processStop() {
        Iterable<ActorRef> children = getContext().getChildren();
        for (ActorRef actorRef : children) {
            actorRef.tell(PoisonPill.getInstance(), getSelf());
        }
    }

    private void processRestart() {
        Iterable<ActorRef> children = getContext().getChildren();
        for (ActorRef actorRef : children) {
            actorRef.tell(PoisonPill.getInstance(), getSelf());
        }
    }

    public ActorRef getMonitorManager() {
        return monitorManager;
    }

    public void setMonitorManager(ActorRef monitorManager) {
        this.monitorManager = monitorManager;
    }

    public ActorRef getClusterListener() {
        return clusterListener;
    }

    public void setClusterListener(ActorRef clusterListener) {
        this.clusterListener = clusterListener;
    }

    public ActorRef getWorld() {
        return world;
    }

    public void setWorld(ActorRef world) {
        this.world = world;
    }

    public ActorRef getElkLogger() {
        return elkLogger;
    }

    public void setElkLogger(ActorRef elkLogger) {
        this.elkLogger = elkLogger;
    }

    public ActorRef getClusterDistributedPub() {
        return clusterDistributedPub;
    }

    public void setClusterDistributedPub(ActorRef clusterDistributedPub) {
        this.clusterDistributedPub = clusterDistributedPub;
    }

    public ActorRef getClusterDistributedSub() {
        return clusterDistributedSub;
    }

    public void setClusterDistributedSub(ActorRef clusterDistributedSub) {
        this.clusterDistributedSub = clusterDistributedSub;
    }

    public ActorRef getDeadLetterRef() {
        return deadLetterRef;
    }

    public void setDeadLetterRef(ActorRef deadLetterRef) {
        this.deadLetterRef = deadLetterRef;
    }

    public ActorSystem getActorSystem() {
        return actorSystem;
    }

    static class selfCreator implements Creator<CodeBrokerSystem> {

        private static final long serialVersionUID = -4506944735716145059L;

        private final ActorSystem actorSystem;

        public selfCreator(ActorSystem actorSystem) {
            super();
            this.actorSystem = actorSystem;
        }

        @Override
        public CodeBrokerSystem create() throws Exception {
            return new CodeBrokerSystem(actorSystem);
        }

    }
}
