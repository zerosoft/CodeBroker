package com.codebroker.api.internal;


import com.codebroker.core.data.IObject;
import com.codebroker.protocol.SerializableType;

/**
 * 系统级组件服务.
 *
 * @author LongJu
 */
public interface IService extends SerializableType {

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

    default IObject handleBackMessage(IObject obj){
        return null;
    }
    /**
     * 获得服务名称.
     *
     * @return the name
     */
    default String getName(){
        return getClass().getSimpleName();
    };


}
