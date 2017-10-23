package com.codebroker.api.event.event;

/**
 * 移除这个主题的监听器
 *
 * @author zero
 */
public class RemoveEventListener {
    public final String topic;

    public RemoveEventListener(String topic) {
        super();
        this.topic = topic;
    }
}
