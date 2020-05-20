package com.codebroker.api.internal;


import com.codebroker.api.IGameWorld;

/**
 * 外层API调用核心.
 *
 * @author LongJu
 */
public interface ManagerLocator {

    <T> T getManager(Class<T> type);

    /**
     * 放入自定义的服务（公用服务）.
     *
     * @param type the new manager
     */
    boolean setManager(IService type);

	IGameWorld getGameWorld();
}
