package com.codebroker.api.event;


/**
 * 默认实现
 * {@link com.codebroker.api.event}
 *
 */
public interface IEvent {
    /**
     * 消息主题
     * @return
     */
    String getTopic();

    /**
     * 消息体内容
     * @return
     */
    Object getMessage();
}
