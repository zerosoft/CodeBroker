package com.codebroker.extensions.service;

import com.codebroker.api.internal.IResultStatusMessage;

public class ResultStatusMessage implements IResultStatusMessage {

    private final Status status;
    private final Object message;

    public ResultStatusMessage(Status status, Object message) {
        this.status = status;
        this.message = message;
    }

    public ResultStatusMessage() {
        this.status = Status.FAIL;
        this.message = null;
    }

    public ResultStatusMessage(Object object) {
        this.status = Status.OK;
        this.message = object;
    }

    public static ResultStatusMessage OK(Object object){
        return new ResultStatusMessage(object);
    }

    public static ResultStatusMessage FAIL(){
        return new ResultStatusMessage();
    }

    public static ResultStatusMessage ERROR(Object object){
        return new ResultStatusMessage(Status.ERROR,object);
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public Object getMessage() {
        return message;
    }
}
