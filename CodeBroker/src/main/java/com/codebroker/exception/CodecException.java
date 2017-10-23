package com.codebroker.exception;

public class CodecException extends CodeBrokerException {
    private static final long serialVersionUID = -9084607336239491702L;

    public CodecException() {
    }

    public CodecException(String message) {
        super(message);
    }

    public CodecException(Throwable t) {
        super(t);
    }
}
