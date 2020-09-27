package com.codebroker.api.event;

import com.codebroker.api.IGameUser;
import com.codebroker.core.data.IObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 处理客户端的内部请求.
 *
 *  @author LongJu
 */
public interface IGameUserEventListener {

    default Logger getGameUserEventListenerLogger(){
        return LoggerFactory.getLogger(IGameUserEventListener.class);
    }
    /**
     * @param gameUser 游戏用户
     * @param event  事件
     */
    default IObject handleEvent(IGameUser gameUser,IEvent event){
        return null;
    }

}
