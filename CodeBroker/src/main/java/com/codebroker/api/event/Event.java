package com.codebroker.api.event;

import com.codebroker.api.internal.IPacket;
import com.codebroker.core.data.IObject;

/**
 * 事件对象
 * @author LongJu
 */
public class Event implements IEvent {

    private String topic;
    private Object message;

    public Event() {
        super();
    }

    public Event(String topic, Object message) {
        super();
        this.topic = topic;
        this.message = message;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }


}
