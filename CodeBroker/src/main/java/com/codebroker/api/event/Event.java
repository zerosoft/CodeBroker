package com.codebroker.api.event;


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

    public Event(String topic) {
        super();
        this.topic = topic;
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
