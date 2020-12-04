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
import com.codebroker.util.PropertiesWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.impl.ConfigImpl;
import jodd.io.FileUtil;
import jodd.io.findfile.FindFile;
import org.apache.tools.ant.FileScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletionStage;
import java.util.jar.JarFile;

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

    public String getPath() throws IOException {
        String path;
        URL url = this.getClass().getProtectionDomain().getCodeSource().getLocation();
        URLConnection connection = url.openConnection();
        if(connection instanceof JarURLConnection) {
            JarFile jarFile = ((JarURLConnection) connection).getJarFile();
            path = jarFile.getName();
            int separator = path.indexOf("!/");
            if (separator > 0) {
                path = path.substring(0, separator);
            }
        } else {
            path = url.getPath();
        }
        return path;
    }

    @Override
    public void init(Object obj) {
        logger.debug("Code Broker Mediator init");
        PropertiesWrapper propertiesWrapper = (PropertiesWrapper) obj;

        String akkaName = propertiesWrapper.getProperty(SystemEnvironment.AKKA_NAME,SystemEnvironment.ENGINE_NAME);
        logger.debug("AKKA_NAME:" + akkaName);

        String arteryHostname = propertiesWrapper.getProperty(SystemEnvironment.ARTERY_HOSTNAME, "127.0.0.1");
        int arteryPort = propertiesWrapper.getIntProperty(SystemEnvironment.ARTERY_PORT, 2551);

        String clusterType = propertiesWrapper.getProperty(SystemEnvironment.CLUSTER_TYPE, "default-game");
        String clusterCenter = propertiesWrapper.getProperty(SystemEnvironment.CLUSTER_CENTER, "default-group");

        int clusterShards = propertiesWrapper.getIntProperty(SystemEnvironment.CLUSTER_SHARDS, 1000);

        Config cg = ConfigFactory.parseString("CodeBroker {\n" +
                        "\takka-kryo-serialization.kryo-initializer = \"com.codebroker.protocol.InitKryoInitializer\"\n" +
                        "\takka {\n" +
                        "\t\tloggers = [\"akka.event.slf4j.Slf4jLogger\"]\n" +
                        "//\t\tlog-config-on-start = on\n" +
                        "\t\tloglevel = \"INFO\"\n" +
                        "//\t\tstdout-loglevel = \"INFO\"\n" +
                        "//\t\tlogging-filter = \"akka.event.slf4j.Slf4jLoggingFilter\"\n" +
                        "//\t\tlog-dead-letters = 10\n" +
                        "//\t\tlog-dead-letters-during-shutdown = on\n" +
                        "\n" +
                        "\n" +
                        "\t\tactor {\n" +
                        "\t\t\tprovider = \"cluster\"\n" +
                        "\n" +
                        "\t\t\tdebug {\n" +
                        "\t\t\t\treceive = off\n" +
                        "\t\t\t\tlifecycle = off\n" +
                        "\t\t\t}\n" +
                        "\t\t\t#序列化器\n" +
                        "\t\t\tserializers {\n" +
                        "\t\t\t\tjackson-json = \"akka.serialization.jackson.JacksonJsonSerializer\"\n" +
                        "//\t\t\t\tcode-broker = \"com.codebroker.protocol.serialization.CodeBrokerRemoteSerializer\"\n" +
                        "\t\t\t\tkryo = \"io.altoo.akka.serialization.kryo.KryoSerializer\"\n" +
                        "\t\t\t\tproto = \"akka.remote.serialization.ProtobufSerializer\"\n" +
                        "\t\t\t}\n" +
                        "\n" +
                        "\t\t\t#对应的绑定关系\n" +
                        "\t\t\tserialization-bindings {\n" +
                        "\t\t\t\t\"com.codebroker.core.actortype.message.IUserManager\" = kryo\n" +
                        "\t\t\t\t\"com.codebroker.core.actortype.message.ISessionActor\" = kryo\n" +
                        "\t\t\t\t\"com.codebroker.core.actortype.message.IServiceActor\" = kryo\n" +
                        "\t\t\t  \"com.codebroker.core.actortype.message.IServiceActor$Reply\" = kryo\n" +
                        "\t\t\t\t\"com.codebroker.core.actortype.message.IServiceActor$HandleMessage\" = kryo\n" +
                        "\n" +
                        "\t\t\t\t\"com.codebroker.core.actortype.message.IUserActor\" = kryo\n" +
                        "\t\t\t\t\"com.codebroker.core.actortype.message.IUserActor$LogicEvent\" = kryo\n" +
                        "\t\t\t\t\"com.codebroker.api.internal.IPacket\" = kryo\n" +
                        "\n" +
                        "\t\t\t\t\"com.codebroker.api.event.Event\"= kryo\n" +
                        "//\t\t\t\t\"com.codebroker.core.data.IObject\" = kryo\n" +
                        "//\t\t\t\t\"com.codebroker.core.data.IArray\" = kryo\n" +
                        "\n" +
                        "\t\t\t\t\"akka.actor.typed.ActorRef\" = kryo\n" +
                        "\t\t\t}\n" +
                        "\n" +
                        "\t\t}\n" +
                        "\n" +
                        "\t\tremote {\n" +
                        "\t\t\tlog-remote-lifecycle-events = on\n" +
                        "\t\t}\n" +
                        "\n" +
                        "\n" +
                        "\t\tcluster {\n" +
                        "//\t\t\tseed-nodes = [\n" +
                        "//\t\t\t\t\"akka://CodeBroker@127.0.0.1:2551\",\n" +
                        "//\t\t\t\t\"akka://CodeBroker@127.0.0.1:2552\"\n" +
                        "//\t\t\t]\n" +
                        "\t\t\tdowning-provider-class = \"akka.cluster.sbr.SplitBrainResolverProvider\"\n" +
                        "//\t\t\tmin-nr-of-members = 2\n" +
                        "\t\t\tlog-info = on\n" +
                        "\t\t\tlog-info-verbose = on\n" +
                        "\t\t\tsharding {\n" +
                        "\t\t\t  \tleast-shard-allocation-strategy.rebalance-absolute-limit = 20\n" +
                        "\t\t\t}\n" +
                        "\n" +
                        "\t\t}\n" +
                        "\n" +
                        "\t}\n" +
                        "\n" +
                        "\tgame-logic {\n" +
                        "\t\t# Dispatcher is the name of the event-based dispatcher\n" +
                        "\t\ttype = Dispatcher\n" +
                        "\t\t# What kind of ExecutionService to use\n" +
                        "\t\texecutor = \"thread-pool-executor\"\n" +
                        "\t\t# Configuration for the thread pool\n" +
                        "\t\tthread-pool-executor {\n" +
                        "\t\t\tcore-pool-size-min = 2\n" +
                        "\t\t\tcore-pool-size-factor = 2.0\n" +
                        "\t\t\tcore-pool-size-max = 10\n" +
                        "\t\t}\n" +
                        "\t\t# Throughput defines the maximum number of messages to be\n" +
                        "\t\t# processed per actor before the thread jumps to the next actor.\n" +
                        "\t\t# Set to 1 for as fair as possible.\n" +
                        "\t\tthroughput = 100\n" +
                        "\t}\n" +
                        "\tgame-service {\n" +
                        "\t\ttype = Dispatcher\n" +
                        "\t\texecutor = \"thread-pool-executor\"\n" +
                        "\t\tthread-pool-executor {\n" +
                        "\t\t\tfixed-pool-size = 2\n" +
                        "\t\t}\n" +
                        "\t\tthroughput = 1\n" +
                        "\t}\n" +
                        "\n" +
                        "  akka.actor.default-blocking-io-dispatcher {\n" +
                        "    type = Dispatcher\n" +
                        "    executor = \"thread-pool-executor\"\n" +
                        "    thread-pool-executor {\n" +
                        "      fixed-pool-size = 2\n" +
                        "    }\n" +
                        "    throughput = 1\n" +
                        "  }\n" +
                        "}"
                );
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
                .withValue(akkaName+".akka.remote.artery.canonical.port",
                        ConfigImpl.fromAnyRef(arteryPort, "网络服务地址端口"));

        Config akkaConfig = ConfigFactory
                .load(config)
                .getConfig(akkaName);
        logger.info(akkaConfig.toString());


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
