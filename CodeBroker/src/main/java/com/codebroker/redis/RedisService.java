package com.codebroker.redis;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codebroker.core.service.BaseCoreService;
import com.codebroker.util.PropertiesWrapper;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * 
 * @author xl
 *
 */
public class RedisService extends BaseCoreService {

	private final Logger log = LoggerFactory.getLogger(RedisService.class);
	/**
	 * 链接
	 */
	private Jedis jedis;
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
	public Jedis getJedis() {
		if (isPool) {
			if (jedisPool == null) {
				jedisPool = new JedisPool(poolConfig, url, port, timeout, password);
			}
			return jedisPool.getResource();
		} else {
			if (jedis == null) {
				jedis = new Jedis(url, port);
			}
			return jedis;
		}
	}

	/**
	 * 将String设置到redis中
	 *
	 * @param key
	 *            键
	 * @param value
	 *            值
	 */
	public void setString(String key, String value) {
		getJedis().set(key, value);
	}

	/**
	 * 通过键获取String
	 *
	 * @param key
	 *            键
	 * @return
	 */
	public String getString(String key) {
		return getJedis().get(key);
	}

	/**
	 * 将List数组存进redis中
	 *
	 * @param key
	 *            键
	 * @param list
	 *            String的List对象
	 * @param isHead
	 *            是否每次插入从头部进行插入
	 */
	public void setList(String key, List<String> list, boolean isHead) {
		String[] arr = new String[list.size()];
		list.toArray(arr);
		// 判断是不是从头部开始插入
		if (isHead) {
			getJedis().lpush(key, arr);
		} else {
			getJedis().rpush(key, arr);
		}
		if (log.isDebugEnabled()) {
			log.debug("redis insert a list of " + key + ":" + list);
		}
	}

	/**
	 * 获取redis中的数组
	 *
	 * @param key
	 *            键
	 * @param start
	 *            开始
	 * @param end
	 *            结束
	 * @return
	 */
	public List<String> getList(String key, long start, long end) {
		return getJedis().lrange(key, start, end);
	}

	/**
	 * redis存储对象
	 *
	 * @param key
	 *            键
	 * @param obj
	 *            对象
	 * @throws IOException
	 */
	public void setObject(String key, Object obj) throws IOException {
		if (obj != null) {
			byte[] bytes = ObjectUtils.ObjectToBytes(obj);
			getJedis().set(key.getBytes(), bytes);
		} else {
			log.error("没有找到类");
			throw new NullPointerException("没有找到该类");
		}
	}

	/**
	 * redis获取对象
	 *
	 * @param key
	 *            键
	 * @param clazz
	 *            获取的对象类型
	 * @param <T>
	 * @return
	 * @throws IOException
	 */
	public <T extends Serializable> T getObject(String key, Class<T> clazz) throws IOException {
		byte[] bytes = getJedis().get(key.getBytes());
		T obj = ObjectUtils.ObjectFromBytes(bytes, clazz);
		return obj;
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
			if (password == null || password.trim().equals("")) {
				jedisPool = new JedisPool(poolConfig, url, port, timeout);
			} else {
				jedisPool = new JedisPool(poolConfig, url, port, timeout, password);
			}
			/**
			 * 设置默认的redis
			 */
			jedis = jedisPool.getResource();
		} else {
			log.debug("开始执行单例redis");
			jedis = new Jedis(url, port);
		}

	}

	@Override
	public String getName() {
		return RedisService.class.getSimpleName();
	}

}
