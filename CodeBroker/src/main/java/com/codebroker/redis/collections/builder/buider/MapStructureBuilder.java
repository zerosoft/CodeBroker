package com.codebroker.redis.collections.builder.buider;

import com.codebroker.redis.collections.MapStructure;
import com.codebroker.redis.collections.builder.impl.MapStructureImpl;
import com.codebroker.redis.collections.exception.RedisBuilderException;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;

public class MapStructureBuilder<T> {

    private Class<T> clazz;

    private String nameSpace;

    private Jedis jedis;

    MapStructureBuilder(Jedis jedis, Class<T> clazz) {
        this.jedis = jedis;
        this.clazz = clazz;
    }

    public MapStructureBuilder<T> withNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
        return this;
    }

    public MapStructure<T> build() {
        if (StringUtils.isBlank(nameSpace)) {
            throw new RedisBuilderException("The nameSpace must be specified");
        }
        return new MapStructureImpl<>(jedis, nameSpace, clazz);
    }
}
