package com.codebroker.core;

import com.codebroker.api.AppContext;
import com.codebroker.api.AppListener;
import com.codebroker.api.classloader.JarLoader;
import com.codebroker.api.internal.*;
import com.codebroker.component.ComponentRegistryImpl;
import com.codebroker.component.service.GeoIPComponent;
import com.codebroker.component.service.AkkaSystemComponent;
import com.codebroker.component.service.NettyComponent;
import com.codebroker.component.service.RedisComponent;
import com.codebroker.component.service.MongoDBComponent;
import com.codebroker.jmx.InstanceMXBean;
import com.codebroker.jmx.ManagementService;
import com.codebroker.setting.SystemEnvironment;
import com.codebroker.util.FileUtil;
import com.codebroker.util.HotSwapClassUtil;
import com.codebroker.util.PropertiesWrapper;
import com.esotericsoftware.reflectasm.MethodAccess;
import javassist.CannotCompileException;
import javassist.NotFoundException;
import jodd.props.Props;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.codebroker.setting.SystemEnvironment.APP_JAR_RELOAD_SECOND;

/**
 * CodeBroker引擎入口.
 *
 * @author LongJu
 */
public class ServerEngine implements InstanceMXBean {

    public static int serverId;
    private static String name;

    private static Logger logger = LoggerFactory.getLogger("ServerEngine");

    private static ServerEngine serverEngine;

    public static ServerEngine getInstance() {
        return serverEngine;
    }

    /**
     * 系统服务器组件(集合).
     */
    private final ComponentRegistryImpl systemRegistry;
    /**
     * 热更新工具
     */
    HotSwapClassUtil hotSwapClassUtil;

    ClassLoader iClassLoader;
    /**
     * 应用上下文.
     */
    private KernelContext kernelContext;
    /**
     * 应用逻辑.
     */
    private PropertiesWrapper propertiesWrapper;

    /**
     * 启动引擎的入口.
     *
     * @param props the props
     * @throws Exception the exception
     */
    protected ServerEngine(Props props) {
        logger.info("init ServerEngine");
        propertiesWrapper = new PropertiesWrapper(props);

        ServerEngine.serverId = propertiesWrapper.getIntProperty(SystemEnvironment.APP_ID, -1);
        // 组件管理器
        systemRegistry = new ComponentRegistryImpl();
        // 应用上下文
        kernelContext = new StartupKernelContext(SystemEnvironment.ENGINE_NAME, systemRegistry, propertiesWrapper);

        logger.debug("ServerEngine start getInstance application");

        createAndStartApplication();
    }


    public static void main(String[] args) throws Exception {
        File root = new File("");
        logger.info("File path=" + root.getAbsolutePath());

        File config;
        if (args.length == 1) {
            config = new File(root.getAbsolutePath() + File.separator + args[0]);
        } else {
            // 确保没有过多的参数
            config = new File(root.getAbsolutePath() + File.separator + "config" + File.separator + "app.properties");
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
     * 创建并启动应用.
     */
    private void createAndStartApplication() {
        // 服务的启动
        logger.debug("ServerEngine start createServices");
        // 启动服务
        createServices();
        // 创建守护周期任务
        logger.debug("ServerEngine start Application");

        FileUtil.printOsEnv();
        // 上层逻辑的启动
        startApplication();
        //服务器关闭的钩子
        Runtime.getRuntime().addShutdownHook(new ShutdownHook(this));
    }

    /**
     * 创建对上层逻辑的服务.
     */
    private void createServices() {
        /**
         * 注册Redis服务
         */
        if (propertiesWrapper.getBooleanProperty("redis", false)) {
            logger.info("redis component init");
            IService redisService = new RedisComponent();
            systemRegistry.addComponent(redisService);

        }
        if (propertiesWrapper.getBooleanProperty("mongodb", false)) {
            logger.info("mongodb component init");
            IService mongoDBComponent = new MongoDBComponent();
            systemRegistry.addComponent(mongoDBComponent);
        }

        IService geoIPService = new GeoIPComponent();
        systemRegistry.addComponent(geoIPService);


        // 如果是网关和单幅模式需要启动网络服务
        IService nettyComponent = new NettyComponent();
        systemRegistry.addComponent(nettyComponent);

        IService akkaSystemComponent = new AkkaSystemComponent();
        // jmx相关启动
        ManagementService managementService = new ManagementService(this);
        ((AkkaSystemComponent) akkaSystemComponent).setManagementService(managementService);
        systemRegistry.addComponent(akkaSystemComponent);


        InternalContext.setManagerLocator(new ManagerLocatorImpl());
        kernelContext = new KernelContext(kernelContext);
        ContextResolver.setTaskState(kernelContext);

        for (Object object : kernelContext.serviceComponents) {
            if (object instanceof IService) {
                try {
                    ((IService) object).init(propertiesWrapper);
                } catch (Exception e) {
                    logger.error("Server Exception", e);
                }
            }
        }

        for (Object object : kernelContext.serviceComponents) {
            if (object instanceof ICoreService) {
                while (!((ICoreService) object).isActive()) {
                    logger.info("Waiting Service {}", ((ICoreService) object).getName());
                    try {
                        Thread.sleep(TimeUnit.SECONDS.toMillis(1L));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
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
        String path = propertiesWrapper.getProperty(SystemEnvironment.APP_JAR_PATH);

        List<ReloadMode> reloadMode = propertiesWrapper.getEnumListProperty(SystemEnvironment.APP_JAR_RELOAD, ReloadMode.class, ReloadMode.NONE);
        if (reloadMode.get(0) == ReloadMode.AUTO) {
            int interval = propertiesWrapper.getIntProperty(APP_JAR_RELOAD_SECOND, 10);
            jarAutoReload(path,interval);
        } else if (reloadMode.get(0) == ReloadMode.MANUAL) {

        }

        try {
            JarLoader jarLoader = new JarLoader();
            iClassLoader = jarLoader.loadClasses(new String[]{path}, ClassLoader.getSystemClassLoader());
        } catch (Exception e) {
            logger.error("ClassLoader error", e);
            System.exit(1);
        }
    }

    private void jarAutoReload(String path, int interval) {
        FileAlterationObserver observer = new FileAlterationObserver(path);
        observer.addListener(new FileAlterationListenerAdaptor() {
            @Override
            public void onFileChange(File file) {
                logger.info("File change");
                try {
                    //旧的关闭
                    kernelContext.getAppListener().destroy(null);

                    AppContext.getGameWorld().restart();
                    // 相关组件初始化
                    for (Object object : kernelContext.managerComponents) {
                        if (object instanceof IService) {
                            try {
                                ((IService) object).destroy(propertiesWrapper);
                                ((IService) object).init(propertiesWrapper);
                            } catch (Exception e) {
                                logger.error("Server Components Exception", e);
                                System.exit(1);
                            }
                        }
                    }

                    //启动新的
                    JarLoader jarLoader = new JarLoader();
                    iClassLoader = jarLoader.loadClasses(new String[]{path}, ClassLoader.getSystemClassLoader());
                    startApplication();

                } catch (Exception e) {
                    logger.error("ClassLoader error", e);
                }
            }
        }); //设置文件变化监听器
        //创建文件变化监听器
        FileAlterationMonitor monitor = new FileAlterationMonitor(interval, observer);
        // 开始监控
        try {
            monitor.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动逻辑层.
     */
    private void startApplication() {
        serverEngine = this;
        // 启动上层逻辑应用
        try {
            Class<?> aClass = iClassLoader.loadClass(propertiesWrapper.getProperty(SystemEnvironment.APP_LISTENER));
            Object o = aClass.newInstance();
            MethodAccess.get(aClass).invoke(o, "init", propertiesWrapper);
            kernelContext.setAppListener((AppListener) o);
        } catch (InstantiationException | ClassNotFoundException | IllegalAccessException e) {
            logger.error("Start Error", e);
            System.exit(1);
        }
    }

    @Override
    public void stopEngine() {
        logger.info("Close kernel");
        new Thread(() -> {
            logger.info("Server shut down");
            boolean shutDown = true;
            if (null != kernelContext.getAppListener()) {
                kernelContext.getAppListener().destroy(1);
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

    @Override
    public String getName() {
        return name;
    }

    public ClassLoader getiClassLoader() {
        return iClassLoader;
    }
}
