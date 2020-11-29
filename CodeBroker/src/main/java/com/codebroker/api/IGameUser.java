package com.codebroker.api;

import com.codebroker.api.event.IEventDispatcher;
import com.codebroker.api.event.IGameUserEventListener;
import com.codebroker.core.data.IObject;

import java.util.Optional;

/**
 * 用户操作接口
 *
 * @author LongJu
 */
public interface IGameUser  {
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
     * 给自己发消息
     * @param message
     */
    void sendMessageToGameUser(IObject message);
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
     * 发送消息到服务 会去查找其他服务器的serverice
     * @param serviceName
     * @param message
     */
    void sendMessageToIService(String serviceName, IObject message);

    /**
     * 发送消息到服务默认是本地服务器
     * @param iService 事件的服务类
     * @param message
     */
    void sendMessageToIService(Class iService, IObject message);
    /**
     * 发送消息到服务 会去查找其他服务器的serverice
     * @param iService 事件的服务类
     * @param message
     */
    void sendMessageToIService(long serverId,Class iService, IObject message);
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


    enum UserEvent{
        LOGIN,LOGOUT,LOSET_CONNECTION,
    }

    void addEventListener(UserEvent userEvent, IGameUserEventListener iGameUserEventListener);

    void addEventListener(String key,IGameUserEventListener iGameUserEventListener);
}
