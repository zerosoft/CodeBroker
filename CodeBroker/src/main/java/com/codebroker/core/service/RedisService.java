package com.codebroker.core.service;

import com.codebroker.util.PropertiesWrapper;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author xl
 */
public class RedisService extends BaseCoreService {

    private final Logger log = LoggerFactory.getLogger(RedisService.class);

    /**
     * 默认127.0.0.1
     */
    private String url = "127.0.0.1";
    /**
     * 默认6379端口
     */
    private int port = 6379;
    /**
     * redis认证密码
     */
    private String password = "123456";
    /**
     * 是否为使用redisPool
     */
    private boolean isPool = false;
    /**
     * 设置默认的超时时间10s
     */
    private int timeout = 1000;
    /**
     * redis池
     */
    private JedisPool jedisPool;

    private GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();

    /**
     * 获取Jedis 实例
     *
     * @return
     */
    public synchronized Jedis getJedis() {
        if (isPool) {
            if (jedisPool == null) {
                jedisPool = new JedisPool(poolConfig, url, port, timeout, password);
            }
            Jedis jedis = jedisPool.getResource();
            return jedis;
        } else {
            return new Jedis(url, port);
        }
    }

    @Override
    public void init(Object obj) {
        PropertiesWrapper wrapper = (PropertiesWrapper) obj;
        // 设置
        log.debug("开始执行默认的配置,使用单例redis");
        System.err.println("cant find redis.properties file,load default set ---->127.0.0.1->32768");
        // 判断是否使用pool 如果不使用则是单例的redis

        isPool = wrapper.getBooleanProperty("redis.isPool", true);
        url = wrapper.getProperty("redis.url");
        port = wrapper.getIntProperty("redis.port", 6379);
        password = wrapper.getProperty("redis.password");
        // 设置全局的配置
        if (isPool) {
            // 设置全局判断变量为true
            log.debug("开始执行pool的创建工作");
            poolConfig.setMaxTotal(50);
            if (password == null || password.trim().equals("")) {
                jedisPool = new JedisPool(poolConfig, url, port, timeout);
            } else {
                jedisPool = new JedisPool(poolConfig, url, port, timeout, password);
            }
            /**
             * 设置默认的redis
             */
        } else {
            log.debug("开始执行单例redis");
        }
        super.setActive();
    }

    @Override
    public String getName() {
        return RedisService.class.getSimpleName();
    }

}
