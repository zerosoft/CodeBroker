package com.codebroker.core.actor;

import java.util.MissingResourceException;

import com.codebroker.core.ContextResolver;
import com.codebroker.core.data.IObject;
import com.codebroker.redis.RedisService;

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

	@Override
	public Receive createReceive() {
		return ReceiveBuilder.create().match(IObject.class, msg -> {
			try {
				RedisService component = ContextResolver.getComponent(RedisService.class);
				Jedis jedis = component.getJedis();
				String json = msg.toJson();
				jedis.lpush(LOG_KEY, json);
			} catch (MissingResourceException e) {
				// TODO: handle exception
			}

		}).build();
	}

}
