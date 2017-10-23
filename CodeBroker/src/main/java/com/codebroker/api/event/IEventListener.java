package com.codebroker.api.event;

public interface IEventListener {
    /**
     * 分发事件
     *
     * @param topic   主题
     * @param iObject 消息体
     */
    public void handleEvent(Event event);

}



