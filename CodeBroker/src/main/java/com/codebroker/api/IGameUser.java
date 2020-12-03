package com.codebroker.api;

import com.codebroker.api.event.IEvent;
import com.codebroker.api.event.IGameUserEventListener;
import com.codebroker.api.internal.IPacket;
import com.codebroker.api.internal.IResultStatusMessage;



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
     * 发生消息给其他IO会话
     * @param message
     */
    void sendMessageToIoSession(IPacket message);

    /**
     * 向自己发送事件
     * @param event
     */
    void sendMessageToSelf(IEvent event);
    /**
     * 发消息给本地服务
     * @param serviceName
     * @param message
     * @return
     */
    IResultStatusMessage sendMessageToLocalIService(String serviceName, IPacket message);
    /**
     * 发消息给本地服务
     * @param iService
     * @param message
     * @return
     */
    IResultStatusMessage sendMessageToLocalIService(Class iService, IPacket message);

    /**
     * 发送消息到服务 会去查找其他服务器的serverice
     * @param serviceName
     * @param message
     */
    void sendMessageToIService(String serviceName, IPacket message);

    /**
     * 发送消息到服务默认是本地服务器
     * @param iService 事件的服务类
     * @param message
     */
    void sendMessageToIService(Class iService, IPacket message);
    /**
     * 发送消息到服务 会去查找其他服务器的serverice
     * @param iService 事件的服务类
     * @param message
     */
    void sendMessageToIService(long serverId,Class iService, IPacket message);
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
        LOGIN,//登入
        LOGOUT,//登出
        LOST_CONNECTION,//失去连接
	}

    void addEventListener(UserEvent userEvent, IGameUserEventListener iGameUserEventListener);

    void addEventListener(String key,IGameUserEventListener iGameUserEventListener);

    void sendEventToSelf(IEvent event);
}
