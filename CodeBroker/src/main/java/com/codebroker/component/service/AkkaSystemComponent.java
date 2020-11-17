package com.codebroker.component.service;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.AskPattern;
import akka.discovery.Discovery;
import akka.discovery.Lookup;
import akka.discovery.ServiceDiscovery;
import com.codebroker.component.BaseCoreService;
import com.codebroker.core.actortype.ActorPathService;
import com.codebroker.core.actortype.GameRootSystem;
import com.codebroker.core.actortype.message.IGameRootSystemMessage;
import com.codebroker.jmx.ManagementService;
import com.codebroker.net.http.HttpServer;
import com.codebroker.setting.SystemEnvironment;
import com.codebroker.util.FileUtil;
import com.codebroker.util.PropertiesWrapper;
import com.google.common.collect.Lists;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigList;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.impl.ConfigImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletionStage;
import akka.management.javadsl.AkkaManagement;
/**
 * Akka的启动类
 *
 * @author LongJu
 */
public class AkkaSystemComponent extends BaseCoreService {

    public static final String CONF_NAME = "config";
    public static final String DEF_AKKA_CONFIG_NAME = "application.conf";
    private static final String DEF_KEY = "CodeBroker";
    private static Logger logger = LoggerFactory.getLogger(AkkaSystemComponent.class);

    private ActorSystem<IGameRootSystemMessage> system;

    private ManagementService managementService;

    @Override
    public void init(Object obj) {
        logger.debug("Code Broker Mediator init");
        File root = new File("");
        String searchPath = root.getAbsolutePath() + File.separator + CONF_NAME;
        logger.debug("conf path:" + searchPath);
        PropertiesWrapper propertiesWrapper = (PropertiesWrapper) obj;

        String property = propertiesWrapper.getProperty(SystemEnvironment.AKKA_FILE_NAME, DEF_AKKA_CONFIG_NAME);

        String filePath = propertiesWrapper.getProperty(SystemEnvironment.AKKA_CONFIG_PATH, searchPath);

        logger.debug("akka conf path:" + filePath);
        File configFile = FileUtil.scanFileByPath(filePath, property);

        String akkaName = propertiesWrapper.getProperty(SystemEnvironment.AKKA_NAME, DEF_KEY);
        logger.debug("AKKA_NAME:" + akkaName);
        String configName = propertiesWrapper.getProperty(SystemEnvironment.AKKA_CONFIG_NAME, DEF_KEY);
        logger.debug("configName:" + configName);


        String arteryHostname = propertiesWrapper.getProperty(SystemEnvironment.ARTERY_HOSTNAME, "127.0.0.1");
        int arteryPort = propertiesWrapper.getIntProperty(SystemEnvironment.ARTERY_PORT, 2551);

        logger.debug("init Actor System start: akkaName=" + akkaName + " configName:" + configName);
        Config cg = ConfigFactory.parseFile(configFile);
//        List<String> roles= Lists.newArrayList();
//        roles.add("Cluster");
        Config config = cg.withValue(akkaName+".akka.remote.artery.canonical.hostname",
                ConfigImpl.fromAnyRef(arteryHostname, "网络服务地址IP"))
                         .withValue(akkaName+".akka.remote.artery.canonical.port",
                ConfigImpl.fromAnyRef(arteryPort, "网络服务地址端口"));

        cg.withFallback(ConfigFactory.defaultReference(Thread.currentThread().getContextClassLoader()));
        Config akkaConfig = ConfigFactory
                .load(cg)
                .getConfig(configName);


        this.system = ActorSystem.create(GameRootSystem.create(propertiesWrapper.getIntProperty(SystemEnvironment.APP_ID,1)), akkaName,akkaConfig);

//        AkkaManagement.get(system.classicSystem()).start();
        HttpServer.start(system);

        ActorPathService.akkaConfig=akkaConfig;

//        System.out.println(akkaConfig.getLong("akka.cluster.sharding.number-of-shards"));

        int http_prot = propertiesWrapper.getIntProperty(SystemEnvironment.HTTP_PORT, 0);

        CompletionStage<IGameRootSystemMessage.Reply> ask = AskPattern.ask(system,
                replyActorRef ->new IGameRootSystemMessage.StartGameRootSystemMessage(replyActorRef),
                Duration.ofMillis(500),
                system.scheduler());
        ask.whenComplete((reply, throwable) -> {
            super.setActive();
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }


    @Override
    public void destroy(Object obj) {
       logger.debug("akka system close");
       system.terminate();
       logger.debug("akka system closed");
    }

    @Override
    public String getName() {
        return AkkaSystemComponent.class.getSimpleName();
    }

    public ActorSystem<IGameRootSystemMessage> getSystem() {
        return system;
    }

    public void setManagementService(ManagementService managementService) {
        this.managementService = managementService;
    }




}
