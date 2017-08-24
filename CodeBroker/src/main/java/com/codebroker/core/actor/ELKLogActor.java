package com.codebroker.core.actor;

import com.codebroker.core.ContextResolver;
import com.codebroker.redis.RedisService;
import com.codebroker.util.JSONUtil;

import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;
import redis.clients.jedis.Jedis;

/**
 * ELK日志传输Actor
 * 
 * @author xl
 *
 */
public class ELKLogActor extends AbstractActor {

	private static final String LOG_KEY = "logstash";

	public static final String IDENTIFY = ELKLogActor.class.getName();

	/**
	 * ELK日志
	 * 
	 * @author xl
	 *
	 */
	public static class ELKSystemLog {
		public String clazzName;

		public String message;

		public ELKSystemLog() {
			super();
		}

	}

	@Override
	public Receive createReceive() {
		return ReceiveBuilder.create().match(ELKSystemLog.class, msg -> {
			RedisService component = ContextResolver.getComponent(RedisService.class);
			if (component != null) {
				Jedis jedis = component.getJedis();
//				jedis.lpush(LOG_KEY, JSONUtil.objectToFastJSON(msg));
			}
		}).build();
	}

}
