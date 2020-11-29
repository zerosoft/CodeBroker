package com.codebroker.extensions.service;

import com.codebroker.api.internal.IPacket;

public class RequestKeyMessage<T,R> implements IPacket<T> {

    private final T key;
    private final R message;

    public RequestKeyMessage(T key, R message) {
        this.key = key;
        this.message = message;
    }

    @Override
    public T getOpCode() {
        return key;
    }

    @Override
    public R getRawData() {
        return message;
    }
}
