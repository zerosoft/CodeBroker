package com.codebroker.api.internal;

import java.io.Serializable;

public class ResultStatusMessage implements IResultStatusMessage{

    private final Status status;
    private final Serializable message;

    public ResultStatusMessage(Status status, Serializable message) {
        this.status = status;
        this.message = message;
    }

    public ResultStatusMessage() {
        this.status = Status.FAIL;
        this.message = null;
    }

    public ResultStatusMessage(Serializable object) {
        this.status = Status.OK;
        this.message = object;
    }

    public static ResultStatusMessage OK(Serializable object){
        return new ResultStatusMessage(object);
    }

    public static ResultStatusMessage FAIL(){
        return new ResultStatusMessage();
    }

    public static ResultStatusMessage ERROR(Serializable object){
        return new ResultStatusMessage(Status.ERROR,object);
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public Serializable getMessage() {
        return message;
    }
}
