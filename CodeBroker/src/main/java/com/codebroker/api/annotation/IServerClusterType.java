package com.codebroker.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface IServerClusterType {
    /**
     * 是否是集群分片的服务
     * @return
     */
    boolean sharding();
}
