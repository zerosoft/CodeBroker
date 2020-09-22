package com.codebroker.api;

import com.codebroker.api.event.IEventDispatcher;
import com.codebroker.core.data.IObject;

import java.util.Optional;

/**
 * 用户操作接口
 *
 * @author LongJu
 */
public interface IGameUser extends IEventDispatcher {
    /**
     * 获得用户id，系统分配
     *
     * @return
     */
    String getUserId();

    /**
     * 发送消息给IO会话
     *
     * @param requestId
     * @param message
     */
    void sendMessageToIoSession(int requestId, Object message);

    Optional<IObject> sendMessageToISession(String serviceName, IObject message);
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

}
