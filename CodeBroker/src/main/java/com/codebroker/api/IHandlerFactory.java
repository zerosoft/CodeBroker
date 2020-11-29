package com.codebroker.api;

import java.util.Optional;

/**
 * 请求的Handler接口.
 *
 * @author LongJu
 */
public interface IHandlerFactory<T> {
    /**
     * 添加请求的handler 类文件
     *
     * @param handlerKey
     * @param class1
     */
    void addHandler(T handlerKey, Class<?> class1);

    /**
     * 添加请求的handler 实例
     *
     * @param handlerKey
     * @param obj
     */
    void addHandler(T handlerKey, Object obj);

    /**
     * 移除handler
     *
     * @param handlerKey
     */
    void removeHandler(T handlerKey);

    /**
     * 查找handler
     *
     * @param handlerKey
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    Optional<Object> findHandler(T handlerKey) throws InstantiationException, IllegalAccessException;

    /**
     * 清除所有的handler
     */
    void clearAll();

}
