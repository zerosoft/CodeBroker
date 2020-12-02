package com.codebroker.component.service;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.AskPattern;
import com.codebroker.component.BaseCoreService;
import com.codebroker.core.ContextResolver;
import com.codebroker.core.ServerEngine;
import com.codebroker.core.actortype.ActorPathService;
import com.codebroker.core.actortype.GameRootSystem;
import com.codebroker.core.actortype.message.IGameRootSystemMessage;
import com.codebroker.jmx.ManagementService;
import com.codebroker.net.http.HttpServer;
import com.codebroker.setting.SystemEnvironment;
import com.codebroker.util.FileUtil;
import com.codebroker.util.PropertiesWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.impl.ConfigImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletionStage;
/**
 * Akka的启动类
 *
 * @author LongJu
 */
public class AkkaSystemComponent extends BaseCoreService {

    private static Logger logger = LoggerFactory.getLogger(AkkaSystemComponent.class);

    public static final String CONF_NAME = "config";
    public static final String DEF_AKKA_CONFIG_NAME = "application.conf";


    private ActorSystem<IGameRootSystemMessage> system;

    private ManagementService managementService;

    @Override
    public void init(Object obj) {
        logger.debug("Code Broker Mediator init");
//        File root = new File("");
//        String searchPath = root.getAbsolutePath() + File.separator + CONF_NAME;
//        logger.debug("conf path:" + searchPath);
        PropertiesWrapper propertiesWrapper = (PropertiesWrapper) obj;

//        String property = propertiesWrapper.getProperty(SystemEnvironment.AKKA_FILE_NAME, DEF_AKKA_CONFIG_NAME);

//        String filePath = propertiesWrapper.getProperty(SystemEnvironment.AKKA_CONFIG_PATH, searchPath);

//        logger.debug("akka conf path:" + filePath);
//        File configFile = FileUtil.scanFileByPath(filePath, property);

        String akkaName = propertiesWrapper.getProperty(SystemEnvironment.AKKA_NAME,SystemEnvironment.ENGINE_NAME);
        logger.debug("AKKA_NAME:" + akkaName);

        String arteryHostname = propertiesWrapper.getProperty(SystemEnvironment.ARTERY_HOSTNAME, "127.0.0.1");
        int arteryPort = propertiesWrapper.getIntProperty(SystemEnvironment.ARTERY_PORT, 2551);

        String clusterType = propertiesWrapper.getProperty(SystemEnvironment.CLUSTER_TYPE, "default-game");
        String clusterCenter = propertiesWrapper.getProperty(SystemEnvironment.CLUSTER_CENTER, "default-group");

        int clusterShards = propertiesWrapper.getIntProperty(SystemEnvironment.CLUSTER_SHARDS, 1000);

        Config cg = ConfigFactory.defaultApplication();
        List<String> roles= Lists.newArrayList();
        roles.add(clusterType);
        //	"akka://CodeBroker@127.0.0.1:2551"
        List<String> clusterNodes= Lists.newArrayList();
        clusterNodes.add("akka://"+akkaName+"@"+arteryHostname+":"+arteryPort);

        Optional<ZookeeperComponent> manager = ContextResolver.getComponent(ZookeeperComponent.class);
        if (manager.isPresent()){
            Optional<Collection<String>> cacheServer = manager.get().getIClusterServiceRegister().getCacheServer(clusterCenter);
            cacheServer.ifPresent(dc->{
                for (String dcString : dc) {
                    clusterNodes.add("akka://"+akkaName+"@"+dcString);
                }
            });
        }

        List<String> loggers= Lists.newArrayList();
        loggers.add( "akka.event.slf4j.Slf4jLogger");

        Config config = cg.withValue(akkaName+".akka.remote.artery.canonical.hostname",
                ConfigImpl.fromAnyRef(arteryHostname, "网络服务地址IP"))
                         .withValue(akkaName+".akka.remote.artery.canonical.port",
                 ConfigImpl.fromAnyRef(arteryPort, "网络服务地址端口"))
                          .withValue(akkaName+".akka.cluster.roles",
                        ConfigImpl.fromAnyRef(roles, "网络集群的角色"))
                         .withValue(akkaName+".akka.cluster.sharding.number-of-shards",
                                ConfigImpl.fromAnyRef(clusterShards, "网络集群因子"))
                        .withValue(akkaName+".akka.cluster.multi-data-center.self-data-center",
                        ConfigImpl.fromAnyRef(clusterCenter, "网络集群所属数据中心"))
                        .withValue(akkaName+".akka.cluster.seed-nodes",
                        ConfigImpl.fromAnyRef(clusterNodes, "网络集群节点"))
                .withValue(akkaName+".akka-kryo-serialization.kryo-initializer",
                ConfigImpl.fromAnyRef("com.codebroker.protocol.InitKryoInitializer", "默认序列化"))
                .withValue(akkaName+".akka.loggers",
                        ConfigImpl.fromAnyRef(loggers, "日志实现类"))
                .withValue(akkaName+".akka.actor.provider",
                        ConfigImpl.fromAnyRef("cluster", "Actor类型"))
                .withValue(akkaName+".akka.actor.serializers.kryo",
                        ConfigImpl.fromAnyRef("io.altoo.akka.serialization.kryo.KryoSerializer", "序列化器"))
//                    .withValue(akkaName+".akka.actor.serializers.serialization-bindings",
//                            ConfigImpl.fromAnyRef("kryo", "序列化器"))
//                .withValue(akkaName+".akka.actor.serializers.serialization-bindings",
//                        ConfigImpl.fromAnyRef(serializationBindings, "序列化器"))
                .withValue(akkaName+".akka.actor.serialization-bindings.\"com.codebroker.core.actortype.message.ISessionActor\"",
                        ConfigImpl.fromAnyRef("kryo", "序列化器"))
                .withValue(akkaName+".akka.actor.serialization-bindings.\"com.codebroker.core.actortype.message.IServiceActor$Reply\"",
                        ConfigImpl.fromAnyRef("kryo", "序列化器"))
                .withValue(akkaName+".akka.actor.serialization-bindings.\"com.codebroker.core.actortype.message.IServiceActor$HandleMessage\"",
                        ConfigImpl.fromAnyRef("kryo", "序列化器"))
                .withValue(akkaName+".akka.actor.serialization-bindings.\"com.codebroker.core.actortype.message.IUserActor\"",
                        ConfigImpl.fromAnyRef("kryo", "序列化器"))
                .withValue(akkaName+".akka.actor.serialization-bindings.\"com.codebroker.core.actortype.message.IUserActor$LogicEvent\"",
                        ConfigImpl.fromAnyRef("kryo", "序列化器"))
                .withValue(akkaName+".akka.actor.serialization-bindings.\"com.codebroker.api.internal.IPacket\"",
                        ConfigImpl.fromAnyRef("kryo", "序列化器"))
                .withValue(akkaName+".akka.actor.serialization-bindings.\"com.codebroker.api.event.Event\"",
                        ConfigImpl.fromAnyRef("kryo", "序列化器"))
                .withValue(akkaName+".akka.actor.serialization-bindings.\"akka.actor.typed.ActorRef\"",
                        ConfigImpl.fromAnyRef("kryo", "序列化器"))

                .withValue(akkaName+".akka.actor.cluster.downing-provider-class",
                        ConfigImpl.fromAnyRef("akka.cluster.sbr.SplitBrainResolverProvider", "集群脑裂管理器"))

                .withValue(akkaName+".game-logic.type",
                        ConfigImpl.fromAnyRef("Dispatcher", "执行器类型"))
                .withValue(akkaName+".game-logic.executor",
                        ConfigImpl.fromAnyRef("thread-pool-executor", "执行器类型"))
                .withValue(akkaName+".game-logic.executor.thread-pool-executor.core-pool-size-min",
                        ConfigImpl.fromAnyRef(2, "最小线程数"))
                .withValue(akkaName+".game-logic.executor.thread-pool-executor.core-pool-size-factor",
                        ConfigImpl.fromAnyRef(2.0, "线程因子"))
                .withValue(akkaName+".game-logic.executor.thread-pool-executor.core-pool-size-max",
                        ConfigImpl.fromAnyRef(10, "最大线程数"))
                .withValue(akkaName+".game-logic.throughput",
                        ConfigImpl.fromAnyRef(1, "集群脑裂管理器"))

                .withValue(akkaName+".game-service.type",
                        ConfigImpl.fromAnyRef("Dispatcher", "执行器类型"))
                .withValue(akkaName+".game-service.executor",
                        ConfigImpl.fromAnyRef("thread-pool-executor", "执行器类型"))
                .withValue(akkaName+".game-service.thread-pool-executor.fixed-pool-size",
                        ConfigImpl.fromAnyRef(2, "执行器类型"))
                .withValue(akkaName+".game-service.throughput",
                        ConfigImpl.fromAnyRef(1, "执行器类型"))

                .withValue(akkaName+".akka.actor.default-blocking-io-dispatcher.type",
                        ConfigImpl.fromAnyRef("Dispatcher", "执行器类型"))
                .withValue(akkaName+".akka.actor.default-blocking-io-dispatcher.executor",
                        ConfigImpl.fromAnyRef("thread-pool-executor", "执行器类型"))
                .withValue(akkaName+".akka.actor.default-blocking-io-dispatcher.thread-pool-executor.fixed-pool-size",
                        ConfigImpl.fromAnyRef(2, "执行器类型"))
                .withValue(akkaName+".akka.actor.default-blocking-io-dispatcher.throughput",
                        ConfigImpl.fromAnyRef(1, "执行器类型"));

        cg.withFallback(ConfigFactory.defaultReference(Thread.currentThread().getContextClassLoader()));
        Config akkaConfig = ConfigFactory
                .load(config)
                .getConfig(akkaName);


        this.system = ActorSystem.create(GameRootSystem.create(propertiesWrapper.getIntProperty(SystemEnvironment.APP_ID,1)), akkaName,akkaConfig);
        ServerEngine.akkaHttpHost = propertiesWrapper.getProperty(SystemEnvironment.AKKA_HTTP_HOSTNAME, "127.0.0.1");
        ServerEngine.akkaHttpPort = propertiesWrapper.getIntProperty(SystemEnvironment.AKKA_HTTP_PORT, 0);

        HttpServer.start(system,ServerEngine.akkaHttpHost,ServerEngine.akkaHttpPort);

        ActorPathService.akkaConfig=akkaConfig;


        CompletionStage<IGameRootSystemMessage.Reply> ask = AskPattern.ask(system,
                replyActorRef ->new IGameRootSystemMessage.StartGameRootSystemMessage(replyActorRef),
                Duration.ofMillis(SystemEnvironment.TIME_OUT_MILLIS),
                system.scheduler());
        ask.whenComplete((reply, throwable) -> {
            super.setActive();
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
        name=getClass().getName();
        setActive();
    }


    @Override
    public void destroy(Object obj) {
       logger.debug("akka system close");
       system.terminate();
       logger.debug("akka system closed");
    }


    public ActorSystem<IGameRootSystemMessage> getSystem() {
        return system;
    }

    public void setManagementService(ManagementService managementService) {
        this.managementService = managementService;
    }


}
