package com.codebroker.core;

import com.codebroker.api.CodeBrokerAppListener;
import com.codebroker.api.internal.IService;
import com.codebroker.api.internal.InternalContext;
import com.codebroker.component.ComponentRegistryImpl;
import com.codebroker.core.eventbus.CodebrokerEnvelope;
import com.codebroker.core.manager.CacheManager;
import com.codebroker.core.manager.GeoIPService;
import com.codebroker.core.manager.JongoDBService;
import com.codebroker.core.service.AkkaBootService;
import com.codebroker.core.service.ICoreService;
import com.codebroker.core.service.NettyNetService;
import com.codebroker.core.service.RedisService;
import com.codebroker.jmx.InstanceMXBean;
import com.codebroker.jmx.ManagementService;
import com.codebroker.setting.SystemEnvironment;
import com.codebroker.util.FileUtil;
import com.codebroker.util.HotSwapClassUtil;
import com.codebroker.util.PropertiesWrapper;
import javassist.CannotCompileException;
import javassist.NotFoundException;
import jodd.props.Props;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * 阿瓦隆，引擎入口.
 *
 * @author ZERO
 */
public class ServerEngine implements InstanceMXBean {

    public static CodebrokerEnvelope envelope = new CodebrokerEnvelope();
    public static int serverId;
    public static String remotePath;
    public static int gameServerNum;
    public static int gateServerNum;
    /**
     * The name.
     */
    private static String name;
    /**
     * The logger.
     */
    private static Logger logger = LoggerFactory.getLogger("AvalonEngine");
    /**
     * 系统服务器组件(集合).
     */
    private final ComponentRegistryImpl systemRegistry;
    HotSwapClassUtil hotSwapClassUtil;
    /**
     * 应用上下文.
     */
    private KernelContext application;
    /**
     * 应用逻辑.
     */
    private CodeBrokerAppListener listener;
    private PropertiesWrapper propertiesWrapper;

    /**
     * 启动引擎的入口.
     *
     * @param props the props
     * @throws Exception the exception
     */
    protected ServerEngine(Props props) throws Exception {
        logger.debug("create AvalonEngine");
        propertiesWrapper = new PropertiesWrapper(props);

        ServerEngine.serverId = propertiesWrapper.getIntProperty(SystemEnvironment.APP_ID, -1);
        // 组件管理器
        systemRegistry = new ComponentRegistryImpl();
        // 应用上下文
        application = new StartupKernelContext(SystemEnvironment.ENGINE_NAME, systemRegistry, propertiesWrapper);
        logger.debug("AvalonEngine start create application");

        createAndStartApplication();
    }

    /**
     * The main method.
     *
     * @param args the arguments
     * @throws Exception the exception
     */
    public static void main(String[] args) throws Exception {
        File root = new File("");
        logger.info("File path=" + root.getAbsolutePath());
        File config = null;
        if (args.length == 1) {
            config = new File(root.getAbsolutePath() + File.separator + args[0]);
        } else {
            // 确保没有过多的参数
            config = new File(root.getAbsolutePath() + File.separator + "conf" + File.separator + "app.properties");
        }
        if (!config.exists()) {
            logger.info("not app.properties int conf");
            System.exit(1);
        }
        // 属性
        Props props = new Props();
        props.load(config);

        name = props.getValue(SystemEnvironment.APP_NAME);
        // 启动核心
        new ServerEngine(props);
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * 创建并启动应用.
     */
    private void createAndStartApplication() {
        // 服务的启动
        logger.debug("AvalonEngine start createServices");
        // 启动服务
        createServices(name);
        // 创建守护周期任务
        logger.debug("AvalonEngine start Application");

        FileUtil.printOsEnv();
        // 上层逻辑的启动
        startApplication(name);

        Runtime.getRuntime().addShutdownHook(new ShutdownHook());
    }

    /**
     * 创建对上层逻辑的服务.
     *
     * @param appName the app name
     */
    private void createServices(String appName) {
        /**
         * 注册Redis服务
         */
        if (propertiesWrapper.getBooleanProperty("redis", false)) {
            System.err.println("reids not need");
            IService redisService = new RedisService();
            systemRegistry.addComponent(redisService);
        }
        if (propertiesWrapper.getBooleanProperty("mongodb", false)) {
            System.err.println("mongodb not need");
            IService jongoDBService = new JongoDBService();
            application.setManager(jongoDBService);
        }
        IService geoIPService = new GeoIPService();
        application.setManager(geoIPService);
        /**
         * 缓存服务
         */
        IService cacheManager = new CacheManager();
        systemRegistry.addComponent(cacheManager);
        // 系统级组件

        IService mediator = new AkkaBootService();
        // 如果是网关和单幅模式需要启动网络服务
        IService netty = new NettyNetService();
        systemRegistry.addComponent(netty);
        // jmx相关启动
        ManagementService managementService = new ManagementService(this);
        ((AkkaBootService) mediator).setManagementService(managementService);
        systemRegistry.addComponent(mediator);


        InternalContext.setManagerLocator(new ManagerLocatorImpl());
        application = new KernelContext(application);
        ContextResolver.setTaskState(application);

        for (Object object : application.serviceComponents) {
            if (object instanceof IService) {
                try {
                    ((IService) object).init(propertiesWrapper);
                } catch (Exception e) {
                    logger.error("Server Exception", e);
                }
            }
        }

        // 相关组件初始化
        for (Object object : application.managerComponents) {
            if (object instanceof IService) {
                try {
                    ((IService) object).init(propertiesWrapper);
                } catch (Exception e) {
                    logger.error("Server Components Exception", e);
                }
            }
        }


        for (Object object : application.serviceComponents) {
            if (object instanceof ICoreService) {
                while (!((ICoreService) object).isActive()) {
                    logger.info("Waiting Service");
                }
                logger.info(((ICoreService) object).getName() + " Start");
            }
        }


        // 是否开启热替换功能
        // jvm
        // -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000
        // 需要jdk下的tool.jar在classpath下
        // boolean openReload =
        // propertiesWrapper.getBooleanProperty(SystemEnvironment.RELOAD,
        // false);
        // if (openReload) {
        // try {
        // hotSwapClassUtil = new HotSwapClassUtil(propertiesWrapper);
        // } catch (IOException | IllegalConnectorArgumentsException e) {
        // logger.error("Hot Swap Util error", e);
        // }
        // }

    }

    /**
     * 启动逻辑层.
     *
     * @param appName the app name
     */
    private void startApplication(String appName) {
        // 启动上层逻辑应用
        listener = (propertiesWrapper).getClassInstanceProperty(SystemEnvironment.APP_LISTENER, CodeBrokerAppListener.class, new Class[]{});
        listener.init(propertiesWrapper);
        application.setAppListener(listener);
    }

    /**
     * Gets the system registry.
     *
     * @return the system registry
     */
    public ComponentRegistryImpl getSystemRegistry() {
        return systemRegistry;
    }

    /**
     * Gets the enable jmx.
     *
     * @return the enable jmx
     */
    public boolean getEnableJMX() {
        return false;
    }

    @Override
    public void stopEngine() {
        logger.info("Close kernel");
        new Thread(new Runnable() {
            @Override
            public void run() {
                logger.info("Server shut down");
                boolean shutDown = true;
                if (null != listener) {
                    listener.destroy(1);
                } else {
                    shutDown = true;
                }
                if (!shutDown) {
                    logger.info("Server Application not shut down");
                    return;
                }
                for (IService iService : systemRegistry) {
                    iService.destroy(null);
                }
            }
        }).start();
    }


    @Override
    public void reloadJar(String fileName) throws FileNotFoundException, IOException {
        hotSwapClassUtil.reloadJar(fileName);
    }

    @Override
    public void reloadClazz(String fileName, String clazzName) throws FileNotFoundException, IOException {
        hotSwapClassUtil.reload(fileName, clazzName);
    }

    @Override
    public void reloadMethod(String clazz, String methodName, String context)
            throws NotFoundException, CannotCompileException, IOException {
        hotSwapClassUtil.reloadMethod(clazz, methodName, context);
    }

}
