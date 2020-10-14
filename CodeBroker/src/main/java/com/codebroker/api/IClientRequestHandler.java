package com.codebroker.api;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 处理客户端的请求.
 *
 * @author LongJu
 */
public interface IClientRequestHandler {
    /**
     * 获得默认的日志
     * @return
     */
    default Logger getClientRequestLogger(){
        return LoggerFactory.getLogger(IClientRequestHandler.class);
    }

    /**
     * 处理客户的请求
     * @param user 游戏用户
     * @param message 网络消息
     */
    void handleClientRequest(IGameUser user, Object message);

}
