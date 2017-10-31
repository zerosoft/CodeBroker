package com.codebroker.core.actor;

import akka.actor.*;
import akka.japi.Creator;
import akka.japi.pf.ReceiveBuilder;
import com.codebroker.core.ContextResolver;
import com.codebroker.core.cluster.ClusterDistributedPub;
import com.codebroker.core.cluster.ClusterDistributedSub;
import com.codebroker.core.cluster.ClusterListener;
import com.codebroker.core.manager.CacheManager;
import com.codebroker.core.message.CommonMessage;
import com.codebroker.core.model.CodeDeadLetter;
import com.codebroker.core.monitor.MonitorManager;
import com.codebroker.exception.NoInstanceException;
import com.codebroker.protocol.ThriftSerializerFactory;
import com.codebroker.util.LogUtil;
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
        CacheManager component = ContextResolver.getComponent(CacheManager.class);
        /**
         * 集群监听
         */
        clusterListener = actorSystem.actorOf(Props.create(ClusterListener.class), ClusterListener.IDENTIFY);
        this.getContext().watch(clusterListener);
        component.setLocalPath(ClusterListener.IDENTIFY, clusterListener);
        /**
         * 错误地址信息
         */
        Props avalonDeadLetterProps = Props.create(CodeDeadLetter.class);
        deadLetterRef = actorSystem.actorOf(avalonDeadLetterProps, CodeDeadLetter.IDENTIFY);
        actorSystem.eventStream().subscribe(deadLetterRef, DeadLetter.class);

        component.setLocalPath(CodeDeadLetter.IDENTIFY, deadLetterRef);
        /**
         * 初始化游戏世界
         */
        world = actorSystem.actorOf(Props.create(WorldActor.class), WorldActor.IDENTIFY);
        this.getContext().watch(world);

        component.setLocalPath(WorldActor.IDENTIFY, world);

        logger.info("World Path=" + world.path().toString());
        /**
         * ELK日志记录
         */
        elkLogger = actorSystem.actorOf(Props.create(ELKLogActor.class), ELKLogActor.IDENTIFY);
        this.getContext().watch(elkLogger);
        LogUtil.elkLog = elkLogger;
        logger.info("ELKActor Path=" + elkLogger.path().toString());
        component.setLocalPath(ELKLogActor.IDENTIFY, elkLogger);
        /**
         * 分布式发布Actor
         */
        Props pub = Props.create(ClusterDistributedPub.class);
        clusterDistributedPub =
                actorSystem.actorOf(pub, ClusterDistributedPub.IDENTIFY);
        this.getContext().watch(clusterDistributedPub);
        component.setLocalPath(ClusterDistributedPub.IDENTIFY, clusterDistributedPub);
        /**
         * 分布式订阅 Actor
         */
        Props sub = Props.create(ClusterDistributedSub.class, "CODE_BORKER_TOPIC");
        clusterDistributedSub =
                actorSystem.actorOf(sub, ClusterDistributedSub.IDENTIFY);
        this.getContext().watch(clusterDistributedSub);
        component.setLocalPath(ClusterDistributedSub.IDENTIFY, clusterDistributedSub);
        /**
         * 数据相关监听Actor
         */
        Props create = Props.create(MonitorManager.class);
        monitorManager = actorSystem.actorOf(create, MonitorManager.IDENTIFY);
        this.getContext().watch(monitorManager);
        component.setLocalPath(MonitorManager.IDENTIFY, monitorManager);
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(CommonMessage.Start.class, msg -> {
                    processStart();
                    getSender().tell(true, getSelf());
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

    public ActorRef getWorld() {
        return world;
    }

    public void setWorld(ActorRef world) {
        this.world = world;
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
