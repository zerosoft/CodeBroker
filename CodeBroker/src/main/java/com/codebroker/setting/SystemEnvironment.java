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

    //com.code.broker.artery.hostname = 127.0.0.1
    public static final String ARTERY_HOSTNAME = APP_ROOT + ".artery.hostname";
    //com.code.broker.artery.port = 2552
    public static final String ARTERY_PORT = APP_ROOT + ".artery.port";

    //com.code.broker.artery.hostname = 127.0.0.1
    public static final String AKKA_HTTP_HOSTNAME = APP_ROOT + ".http.hostname";
    //com.code.broker.artery.port = 2552
    public static final String AKKA_HTTP_PORT = APP_ROOT + ".http.port";

    //com.code.broker.server.type=game
    public static final String CLUSTER_TYPE = APP_ROOT + ".cluster.type";
    // com.code.broker.server.center=north
    public static final String CLUSTER_CENTER = APP_ROOT + ".cluster.center";
    //com.code.broker.server.shards=100
    public static final String CLUSTER_SHARDS = APP_ROOT + ".cluster.shards";

    //com.code.broker.zookeeper.host=127.0.0.1
    public static final String ZOOKEEPER_HOST = APP_ROOT + ".zookeeper.host";
    //com.code.broker.zookeeper.port=2181
    public static final String ZOOKEEPER_PORT = APP_ROOT + ".zookeeper.port";
    /**
     * Netty网络配置
     */
    public static final String NETTY_BOSS_GROUP_NUM = APP_ROOT + ".netty.boss.group";
    public static final String NETTY_WORKER_GROUP_NUM = APP_ROOT + ".netty.worker.group";
    public static final String NETTY_BACKLOG = APP_ROOT + ".netty.backlog";
    public static final String NETTY_SERVER_NAME = APP_ROOT + ".netty.server.name";


    public static final String TCP_PORT = APP_ROOT + ".netty.tcp.port";
    public static final String HTTP_PORT = APP_ROOT +".http.port";
    /**
     * redis缓存配置
     */
    //com.code.broker.redis.host=127.0.0.1
    public static final String REDIS_URL = APP_ROOT + ".redis.url";
    //com.code.broker.redis.port=2181
    public static final String REDIS_PORT = APP_ROOT + ".redis.port";
    public static final String REDIS_POOL = APP_ROOT + ".redis.isPool";
    public static final String REDIS_PASSWORD = APP_ROOT + ".redis.password";
}
