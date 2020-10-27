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

    /**
     * 发生消息给其他GameUser
     * @param userId
     * @param message
     */
    void sendMessageToGameUser(String userId,IObject message);

    /**
     * 发消息给本地服务
     * @param serviceName
     * @param message
     * @return
     */
    Optional<IObject> sendMessageToLocalIService(String serviceName, IObject message);
    /**
     * 发消息给本地服务
     * @param iService
     * @param message
     * @return
     */
    Optional<IObject> sendMessageToLocalIService(Class iService, IObject message);

    /**
     * 发送消息到服务
     * @param serviceName
     * @param message
     */
    void sendMessageToIService(String serviceName, IObject message);

    /**
     * 发送消息到服务
     * @param iService 事件的服务类
     * @param message
     */
    void sendMessageToIService(Class iService, IObject message);
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
