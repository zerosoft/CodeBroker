package com.codebroker.redis.collections.exception;

public class IrregularKeyValue extends RuntimeException {

    private static final long serialVersionUID = 6161854579438859925L;

    public IrregularKeyValue(String message) {
        super(message);
    }
}