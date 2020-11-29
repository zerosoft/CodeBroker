package com.codebroker.api.event;

import com.codebroker.api.internal.IPacket;
import com.codebroker.core.data.IObject;

public interface IEvent {
    /**
     * 消息
     * @return
     */
    String getTopic();

    /**
     * 消息体
     * @return
     */
    Object getMessage();
}
