package com.codebroker.api.event;

import com.codebroker.core.data.IObject;

public interface IEvent {
     String GAME_EVENT="GameEvent";
    /**
     * 消息
     * @return
     */
    String getTopic();

    /**
     * 消息体
     * @return
     */
    IObject getMessage();
}
