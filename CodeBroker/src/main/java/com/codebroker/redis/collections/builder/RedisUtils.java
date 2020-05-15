package com.codebroker.redis.collections.builder;

import com.codebroker.redis.collections.exception.IrregularKeyValue;
import org.apache.commons.lang3.StringUtils;

public abstract class RedisUtils {


    public static String createKeyWithNameSpace(String key, String nameSpace) {
        if (StringUtils.isBlank(key)) {
            throw new IrregularKeyValue("Key in Key`s value Structure can non`t be empty");
        }
        return nameSpace + ":" + key;
    }

}
