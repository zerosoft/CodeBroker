package com.codebroker.core.remote;

import com.codebroker.exception.NoServerException;

/**
 * 远程管理服务
 *
 * @author LongJu
 */
public interface IRemoteActorManager {
    /**
     * 注册一个接口类和他的实现类
     *
     * @param interfaceClazz
     * @param interfaceHandler
     */
    public void regeditHandler(@SuppressWarnings("rawtypes") Class interfaceClazz,
                               @SuppressWarnings("rawtypes") Class interfaceHandler);

    /**
     * 通过类全名找到其代理类
     *
     * @param clazz
     * @param serverId
     * @return
     * @throws NoServerException
     */
    public <T> T getProxyHandler(@SuppressWarnings("rawtypes") Class clazz, int serverId) throws NoServerException;

}
