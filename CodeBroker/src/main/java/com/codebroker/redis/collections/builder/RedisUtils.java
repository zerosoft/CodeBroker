package com.codebroker.redis.collections.builder;

import com.codebroker.redis.collections.exception.IrregularKeyValue;
import org.apache.commons.lang3.StringUtils;

public abstract class RedisUtils {

    private static final String PREFIX_NAMESPACE = "redis_collection:";

    public static String createKeyWithNameSpace(String key, String nameSpace) {
        if (StringUtils.isBlank(key)) {
            throw new IrregularKeyValue("Key in KeyvalueStructure cannont be empty");
        }
        return PREFIX_NAMESPACE + nameSpace + ":" + key;
    }

    public static String createNameSpace(String nameSpace) {
        return PREFIX_NAMESPACE + nameSpace;
    }
}
