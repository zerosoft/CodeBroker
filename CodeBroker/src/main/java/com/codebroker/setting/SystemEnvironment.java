package com.codebroker.setting;


public class SystemEnvironment {

    /**
     * The Constant ENGINE_NAME.
     */
    public static final String ENGINE_NAME = "CodeBroker";

    /**
     * 配置文件根
     */
    public static final String APP_ROOT = "com.code.broker";

    public static final String APP_ID = APP_ROOT + ".app.id";
    public static final String APP_NAME = APP_ROOT + ".app.name";

    public static final String DEBUG = APP_ROOT + ".debug";
    public static final String APP_LISTENER = APP_ROOT + ".app.listener";
    public static final String APP_JAR_PATH = APP_ROOT + ".app.jar.path";
    /**
     * 热加载配置
     */
    public static final String APP_JAR_RELOAD = APP_ROOT + ".app.jar.reload";
    public static final String APP_JAR_RELOAD_SECOND = APP_ROOT + ".app.jar.reload.second";
    public static final String AKKA_NAME = APP_ROOT + ".akka.name";

    /**
     * akka的配置文件路径
     */
    public static final String AKKA_CONFIG_NAME = APP_ROOT + ".akka.config";
    public static final String AKKA_FILE_NAME = APP_ROOT + ".akka.config.name";

    /**
     * The Constant AKKA_CONFIG_PATH.
     */
    public static final String AKKA_CONFIG_PATH = AKKA_CONFIG_NAME + ".config.path";

    /**
     * Netty网络配置
     */
    public static final String NETTY_BOSS_GROUP_NUM = APP_ROOT + ".netty.boss.group";
    public static final String NETTY_WORKER_GROUP_NUM = APP_ROOT + ".netty.worker.group";
    public static final String NETTY_BACKLOG = APP_ROOT + ".netty.backlog";
    public static final String NETTY_SERVER_NAME = APP_ROOT + ".netty.server.name";


    public static final String TCP_PORT = APP_ROOT + ".netty.tcp.port";
    public static final String HTTP_PORT = APP_ROOT +".http.port";

}
