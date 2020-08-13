package com.codebroker.api.internal;

/**
 * 系统组件管理器.
 *
 *  @author LongJu
 */
public interface ComponentRegistry extends Iterable<IService> {

    /**
     * 根据CLASS类型获得已经注册的组件.
     *
     * @param <T>  the generic type
     * @param type the type
     * @return the component
     */
    <T> T getComponent(Class<T> type);

    /**
     * 移除组件
     * @param type
     */
    void removeComponent(Class type);
}
