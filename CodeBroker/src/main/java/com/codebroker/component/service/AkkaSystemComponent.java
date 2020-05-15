package com.codebroker.component.service;

import akka.actor.typed.ActorSystem;
import com.codebroker.component.BaseCoreService;
import com.codebroker.core.actortype.GameWorld;
import com.codebroker.core.actortype.message.IWorldMessage;
import com.codebroker.jmx.ManagementService;
import com.codebroker.setting.SystemEnvironment;
import com.codebroker.util.FileUtil;
import com.codebroker.util.PropertiesWrapper;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

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

    private ActorSystem<IWorldMessage> system;

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
        File config = FileUtil.scanFileByPath(filePath, property);

        String akkaName = propertiesWrapper.getProperty(SystemEnvironment.AKKA_NAME, DEF_KEY);
        logger.debug("AKKA_NAME:" + akkaName);
        String configName = propertiesWrapper.getProperty(SystemEnvironment.AKKA_CONFIG_NAME, DEF_KEY);
        logger.debug("configName:" + configName);


        logger.debug("init Actor System start: akkaName=" + akkaName + " configName:" + configName);
        Config cg = ConfigFactory.parseFile(config);

        cg.withFallback(ConfigFactory.defaultReference(Thread.currentThread().getContextClassLoader()));
        Config configFile = ConfigFactory.load(cg).getConfig(configName);

        this.system = ActorSystem.create(GameWorld.create(propertiesWrapper.getIntProperty(SystemEnvironment.APP_ID,1)), akkaName,configFile);

        system.tell(IWorldMessage.StartWorldMessage.NO_INSTANCE);
        super.setActive();
    }


    @Override
    public void destroy(Object obj) {
       logger.debug("akkasystem close");
       system.terminate();
       logger.debug("akkasystem closed");
    }

    @Override
    public String getName() {
        return AkkaSystemComponent.class.getSimpleName();
    }

    public ActorSystem<IWorldMessage> getSystem() {
        return system;
    }

    public ManagementService getManagementService() {
        return managementService;
    }

    public void setManagementService(ManagementService managementService) {
        this.managementService = managementService;
    }




}
