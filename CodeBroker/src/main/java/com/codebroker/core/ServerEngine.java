package com.codebroker.core;

import com.codebroker.api.AppListener;
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
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.tools.ant.taskdefs.Classloader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * CodeBroker引擎入口.
 *
 * @author LongJu
 */
public class ServerEngine implements InstanceMXBean {

    public static int serverId;
    private static String name;

    private static Logger logger = LoggerFactory.getLogger("ServerEngine");
    /**
     * 系统服务器组件(集合).
     */
    private final ComponentRegistryImpl systemRegistry;
    /**
     * 热更新工具
     */
    HotSwapClassUtil hotSwapClassUtil;
	static ClassLoader iClassLoader;
    /**
     * 应用上下文.
     */
    private KernelContext application;
    /**
     * 应用逻辑.
     */
    private AppListener listener;
    private PropertiesWrapper propertiesWrapper;

    /**
     * 启动引擎的入口.
     *
     * @param props the props
     * @throws Exception the exception
     */
    protected ServerEngine(Props props){
        logger.info("init ServerEngine");
        propertiesWrapper = new PropertiesWrapper(props);

        ServerEngine.serverId = propertiesWrapper.getIntProperty(SystemEnvironment.APP_ID, -1);
        // 组件管理器
        systemRegistry = new ComponentRegistryImpl();
        // 应用上下文
        application = new StartupKernelContext(SystemEnvironment.ENGINE_NAME, systemRegistry, propertiesWrapper);

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
        createServices(name);
        // 创建守护周期任务
        logger.debug("ServerEngine start Application");


        FileUtil.printOsEnv();
        // 上层逻辑的启动
        startApplication(name);
        //服务器关闭的钩子
        Runtime.getRuntime().addShutdownHook(new ShutdownHook(this));
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
                    System.exit(1);
                }
            }
        }


        for (Object object : application.serviceComponents) {
            if (object instanceof ICoreService) {
                while (!((ICoreService) object).isActive()) {
                    logger.info("Waiting Service {}",((ICoreService) object).getName());
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
        String path = "E:\\github\\CodeBroker\\account_server\\build\\libs";
        FileAlterationObserver fileAlterationObserver=new FileAlterationObserver(path,
                FileFilterUtils.and(
                FileFilterUtils.fileFileFilter(),
                FileFilterUtils.suffixFileFilter(".jar")),  //过滤文件格式
                null);
        FileAlterationObserver observer = new FileAlterationObserver(path);

        long interval = TimeUnit.SECONDS.toMillis(5);
        observer.addListener(new FileAlterationListenerAdaptor() {


            @Override
            public void onFileChange(File file) {
                logger.info("File change");
                try {
                    listener.destroy(null);


                    JarLoader jarLoader = new JarLoader();
                    iClassLoader = jarLoader.loadClasses(new String[]{path}, iClassLoader);

                } catch (Exception e) {
                    logger.error("ClassLoader error",e);
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

        try {
			JarLoader jarLoader = new JarLoader();

            iClassLoader = jarLoader.loadClasses(new String[]{path}, ClassLoader.getSystemClassLoader());
		} catch (Exception e) {
            logger.error("ClassLoader error",e);
        }
    }

    /**
     * 启动逻辑层.
     *
     * @param appName the app name
     */
    private void startApplication(String appName) {

        // 启动上层逻辑应用
//        listener = (propertiesWrapper).getClassInstanceProperty(SystemEnvironment.APP_LISTENER, AppListener.class, new Class[]{});

        try {
            Class<?> aClass = iClassLoader.loadClass(propertiesWrapper.getProperty(SystemEnvironment.APP_LISTENER));
            Object o = aClass.newInstance();
            MethodAccess.get(aClass).invoke(o,"init",propertiesWrapper);
            application.setAppListener((AppListener) o);
        } catch (InstantiationException | ClassNotFoundException |IllegalAccessException e) {
            e.printStackTrace();
        }

//        listener.init(propertiesWrapper);
//        application.setAppListener(listener);
    }

    public ComponentRegistryImpl getSystemRegistry() {
        return systemRegistry;
    }

    public boolean getEnableJMX() {
        return false;
    }

    @Override
    public void stopEngine() {
        logger.info("Close kernel");
        new Thread(() -> {
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

    public static ClassLoader getiClassLoader() {
        return iClassLoader;
    }
}
