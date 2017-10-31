package com.codebroker.api;

import com.codebroker.core.data.IObject;

/**
 * 用户操作接口
 *
 * @author xl
 */
public interface IUser {
    /**
     * 获得用户id
     *
     * @return
     * @throws Exception
     */
    String getUserId();


    /**
     * 发送消息给IO会话
     *
     * @param requestId
     * @param message
     */
    void sendMessageToIoSession(int requestId, Object message);

    /**
     * 主动断开链接
     */
    void disconnect();

    /**
     * 会话是否连通网络
     *
     * @return
     */
    boolean isConnected();

    /**
     * 获取IObject
     *
     * @return
     */
    IObject getIObject();

}
