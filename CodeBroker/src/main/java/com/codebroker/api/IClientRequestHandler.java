package com.codebroker.api;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 处理客户端的请求.
 *
 * @author LongJu
 */
public interface IClientRequestHandler<T> {

    default Logger getClientRequestLogger(){
        return LoggerFactory.getLogger(IClientRequestHandler.class);
    }

    void handleClientRequest(IGameUser user, T message);

}
