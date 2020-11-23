package com.codebroker.component.service;

import com.codebroker.component.BaseCoreService;
import com.codebroker.setting.SystemEnvironment;
import com.codebroker.util.PropertiesWrapper;
import com.google.common.base.Strings;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 *  @author LongJu
 */
public class RedisComponent extends BaseCoreService {

    private final Logger log = LoggerFactory.getLogger(RedisComponent.class);

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
        isPool = wrapper.getBooleanProperty(SystemEnvironment.REDIS_POOL, true);
        url = wrapper.getProperty(SystemEnvironment.REDIS_URL);
        port = wrapper.getIntProperty(SystemEnvironment.REDIS_PORT, 6379);
        password = wrapper.getProperty(SystemEnvironment.REDIS_PASSWORD);
        // 设置全局的配置
        if (isPool) {
            // 设置全局判断变量为true
            log.debug("开始执行pool的创建工作");
            poolConfig.setMaxTotal(50);
            if (Strings.isNullOrEmpty(password)) {
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
        return RedisComponent.class.getName();
    }

}
