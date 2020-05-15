package com.codebroker.api.event;

public interface IEvent {
    String getTopic();
    Object getMessage();
}
