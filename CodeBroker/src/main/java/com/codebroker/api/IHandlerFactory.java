package com.codebroker.api;

/**
 * 请求的Hander接口.
 *
 * @author LongJu
 */
public interface IHandlerFactory {
    /**
     * 添加请求的handler 类文件
     *
     * @param handlerKey
     * @param class1
     */
    void addHandler(int handlerKey, Class<?> class1);

    /**
     * 添加请求的handler 实例
     *
     * @param handlerKey
     * @param obj
     */
    void addHandler(int handlerKey, Object obj);

    /**
     * 移除handler
     *
     * @param handlerKey
     */
    void removeHandler(int handlerKey);

    /**
     * 查找handler
     *
     * @param handlerKey
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    Object findHandler(int handlerKey) throws InstantiationException, IllegalAccessException;

    /**
     * 清除所有的handler
     */
    void clearAll();

}
