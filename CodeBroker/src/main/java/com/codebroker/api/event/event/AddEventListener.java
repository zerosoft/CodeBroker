package com.codebroker.api.event.event;

import com.codebroker.api.event.IEventListener;

/**
 * 增加一个事件监听器
 *
 * @author zero
 */
public class AddEventListener {
    public final String topic;
    public final IEventListener paramIEventListener;

    public AddEventListener(String topic, IEventListener paramIEventListener) {
        super();
        this.topic = topic;
        this.paramIEventListener = paramIEventListener;
    }
}
