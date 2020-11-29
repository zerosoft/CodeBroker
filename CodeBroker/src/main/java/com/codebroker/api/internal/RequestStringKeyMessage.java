package com.codebroker.api.internal;

import java.io.Serializable;

public class RequestStringKeyMessage implements IRequestKeyMessage<String>{

    private final String key;
    private final Serializable  message;

    public RequestStringKeyMessage(String key, Serializable message) {
        this.key = key;
        this.message = message;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public Serializable message() {
        return message;
    }
}
