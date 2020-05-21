package com.codebroker.api.internal;


import com.codebroker.core.data.IObject;

/**
 * 系统级组件服务.
 *
 * @author LongJu
 */
public interface IService {

    /**
     * 初始化.
     *
     * @param obj the obj
     */
    void init(Object obj);

    /**
     * 销毁.
     *
     * @param obj the obj
     */
    void destroy(Object obj);

    /**
     * 处理消息.
     *
     * @param obj the obj
     */
    void handleMessage(IObject obj);

    /**
     * 获得服务名称.
     *
     * @return the name
     */
    String getName();


}
