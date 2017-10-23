package com.codebroker.redis.collections.builder.buider;

import com.codebroker.redis.collections.QueueStructure;
import com.codebroker.redis.collections.builder.impl.QueueStructureImpl;
import com.codebroker.redis.collections.exception.RedisBuilderException;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;

public class QueueStructureBuilder<T> {

    private Class<T> clazz;

    private String nameSpace;

    private Jedis jedis;

    QueueStructureBuilder(Jedis jedis, Class<T> clazz) {
        this.jedis = jedis;
        this.clazz = clazz;
    }

    public QueueStructureBuilder<T> withNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
        return this;
    }

    public QueueStructure<T> build() {
        if (StringUtils.isBlank(nameSpace)) {
            throw new RedisBuilderException("The nameSpace must be specified");
        }
        return new QueueStructureImpl<>(jedis, clazz, nameSpace);
    }
}
