package com.codebroker.api.event;

import com.codebroker.api.IGameUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 处理客户端的内部请求.
 *
 *  @author LongJu
 */
public interface IGameUserEventListener<T> {

    default Logger getGameUserEventListenerLogger(){
        return LoggerFactory.getLogger(IGameUserEventListener.class);
    }
    /**
     * @param gameUser 游戏用户
     * @param eventMessage  事件
     */
    void handleEvent(IGameUser gameUser,T eventMessage);

}
