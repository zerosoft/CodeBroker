package com.codebroker.api.internal;

import java.io.Serializable;

public class RequestIntegerKeyMessage implements IRequestKeyMessage<Integer>{

    private final Integer key;
    private final Serializable  message;

    public RequestIntegerKeyMessage(Integer key, Serializable message) {
        this.key = key;
        this.message = message;
    }

    @Override
    public Integer getKey() {
        return key;
    }

    @Override
    public Serializable message() {
        return message;
    }
}
