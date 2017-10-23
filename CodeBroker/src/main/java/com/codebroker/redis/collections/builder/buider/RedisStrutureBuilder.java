package com.codebroker.redis.collections.builder.buider;

import com.codebroker.redis.collections.builder.KeyValueBuilder;
import redis.clients.jedis.Jedis;

import java.util.Objects;

/**
 * redis结构构造器
 *
 * @author xl
 */
public class RedisStrutureBuilder {

    public static <T> KeyValueBuilder<T> ofKeyValue(Jedis jedis, Class<T> clazz) {
        Objects.requireNonNull(jedis);
        Objects.requireNonNull(clazz, "The class must be specificed to do the RedisStruturebuilder");
        return new KeyValueBuilder<>(jedis, clazz);
    }

    public static CountStructureBuilder ofCount(Jedis jedis) {
        Objects.requireNonNull(jedis);
        return new CountStructureBuilder(jedis);
    }

    public static <T> ListStructureBuilder<T> ofList(Jedis jedis, Class<T> clazz) {
        Objects.requireNonNull(jedis);
        Objects.requireNonNull(clazz, "The class must be specificed to do the ListStruturebuilder");
        return new ListStructureBuilder<>(jedis, clazz);
    }

    public static <T> SetStructureBuilder<T> ofSet(Jedis jedis, Class<T> clazz) {
        Objects.requireNonNull(jedis);
        Objects.requireNonNull(clazz, "The class must be specificed to do the SetStruturebuilder");
        return new SetStructureBuilder<>(jedis, clazz);
    }

    public static <T> MapStructureBuilder<T> ofMap(Jedis jedis, Class<T> clazz) {
        Objects.requireNonNull(jedis);
        Objects.requireNonNull(clazz, "The class must be specificed to do the MapStruturebuilder");
        return new MapStructureBuilder<>(jedis, clazz);

    }

    public static <T> QueueStructureBuilder<T> ofQueue(Jedis jedis, Class<T> clazz) {
        Objects.requireNonNull(jedis);
        Objects.requireNonNull(clazz);
        return new QueueStructureBuilder<>(jedis, clazz);
    }

    public static RankingStructureBuilder ofRanking(Jedis jedis) {
        Objects.requireNonNull(jedis);
        return new RankingStructureBuilder(jedis);
    }

}
