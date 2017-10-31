package com.codebroker.redis;

import com.codebroker.core.service.RedisService;
import com.codebroker.redis.collections.ListStructure;
import com.codebroker.redis.collections.builder.buider.RedisStrutureBuilder;
import com.codebroker.redis.model.TAdb;
import com.codebroker.util.PropertiesWrapper;
import jodd.props.Props;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.util.List;

public class TestRedisList {
    static Jedis jedis;
    private static ListStructure<TAdb> adbs;
    private static ListStructure<String> adbstirng;

    @Before
    public void setUp() throws Exception {
        Props properties = new Props();
        properties.setValue("redis.isPool", "true");
        properties.setValue("redis.url", "192.168.0.199");
        properties.setValue("redis.port", "32768");
        properties.setValue("redis.password", "");
        PropertiesWrapper propertiesWrapper = new PropertiesWrapper(properties);
        RedisService redisService = new RedisService();
        redisService.init(propertiesWrapper);
        jedis = redisService.getJedis();
        adbs = RedisStrutureBuilder.ofList(jedis, TAdb.class).withNameSpace("list_adb").build();
        adbstirng = RedisStrutureBuilder.ofList(jedis, String.class).withNameSpace("list_adb").build();
    }

    @Test
    public void name() throws Exception {
        List<String> tt22 = adbstirng.get("TT22");
        tt22.add("tt1");
        tt22.add("sfsf");
        List<TAdb> tt1 = adbs.get("TT1");
        for (TAdb t1 : tt1) {
//            tt1.remove(t1);
            t1.setValue(441224L);
//            tt1.add(t1);
            tt1.set(tt1.indexOf(t1), t1);
        }

    }
}
