package com.codebroker.api.internal;

/**
 * 内核服务
 */
public interface ICoreService extends IService  {
    /***
     * 是否运行
     * @return
     */
    boolean isActive();

}
