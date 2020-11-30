package com.codebroker.api.event;


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
