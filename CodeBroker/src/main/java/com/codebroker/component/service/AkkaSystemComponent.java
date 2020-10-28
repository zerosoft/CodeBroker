package com.codebroker.component.service;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.AskPattern;
import com.codebroker.component.BaseCoreService;
import com.codebroker.core.actortype.GameRootSystem;
import com.codebroker.core.actortype.message.IGameRootSystemMessage;
import com.codebroker.jmx.ManagementService;
import com.codebroker.net.http.HttpServer;
import com.codebroker.setting.SystemEnvironment;
import com.codebroker.util.FileUtil;
import com.codebroker.util.PropertiesWrapper;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.Duration;
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


        logger.debug("init Actor System start: akkaName=" + akkaName + " configName:" + configName);
        Config cg = ConfigFactory.parseFile(configFile);

        cg.withFallback(ConfigFactory.defaultReference(Thread.currentThread().getContextClassLoader()));
        Config akkaConfig = ConfigFactory.load(cg).getConfig(configName);


        this.system = ActorSystem.create(GameRootSystem.create(propertiesWrapper.getIntProperty(SystemEnvironment.APP_ID,1)), akkaName,akkaConfig);

        AkkaManagement.get(system.classicSystem()).start();
        HttpServer.start(system);

        int http_prot = propertiesWrapper.getIntProperty(SystemEnvironment.HTTP_PORT, 0);

        if (http_prot>0){
//            final Http http = Http.get(system);
//
//            //In order to access all directives we need an instance where the routes are define.
//            HttpDirectives app = new HttpDirectives();
//
//            final CompletionStage<ServerBinding> binding =
//                    http.newServerAt("0.0.0.0", http_prot)
//                            .bind(app.createRoute());
        }

//        binding
//                .thenCompose(ServerBinding::unbind) // trigger unbinding from the port
//                .thenAccept(unbound -> system.terminate()); // and shutdown when done
        //集群单节点
//        ClusterSingleton singleton = ClusterSingleton.get(system);
        // Start if needed and provide a proxy to a named singleton
//        ActorRef<Counter.Command> proxy =
//                singleton.init(SingletonActor.of(Counter.create("11"), "GlobalCounter"));

        //监督策略的单节点
//        ClusterSingleton singleton = ClusterSingleton.get(system);
//        ActorRef<Counter.Command> proxy =
//                singleton.init(
//                        SingletonActor.of(
//                                Behaviors.supervise(Counter.create(""))
//                                        .onFailure(
//                                                SupervisorStrategy.restartWithBackoff(
//                                                        Duration.ofSeconds(1), Duration.ofSeconds(10), 0.2)),
//                                "GlobalCounter"));
//
//        proxy.tell(Counter.Increment.INSTANCE);

//        ExternalShardAllocationClient counter = ExternalShardAllocation.get(system).getClient("Counter");
//
//        ClusterSharding clusterSharding = ClusterSharding.get(system);
//        EntityTypeKey<Counter.Command> typeKey = EntityTypeKey.create(Counter.Command.class, "Counter");
//
//        ActorRef<ShardingEnvelope<Counter.Command>> shardRegion =clusterSharding.init(Entity.of(typeKey, ctx -> {
//            String ctxEntityId = ctx.getEntityId();
//            Behavior<Counter.Command> commandBehavior = Counter.create(ctxEntityId);
//            return commandBehavior;
//        }));
//
//        System.out.println(shardRegion.path().toSerializationFormat());
////        EntityRef<Counter.Command> counterOne = clusterSharding.entityRefFor(typeKey, "counter-1");
////        counterOne.tell(Counter.Increment.INSTANCE);
//
//        shardRegion.tell(new ShardingEnvelope<>("counter-1", Counter.Increment.INSTANCE));
//        shardRegion.tell(new ShardingEnvelope<>("counter-2", Counter.Increment.INSTANCE));
//
//
//        HelloWorldService helloWorldService = new HelloWorldService(system);
//        helloWorldService.sayHello("123","2323");
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
