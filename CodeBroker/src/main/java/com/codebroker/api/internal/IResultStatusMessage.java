package com.codebroker.api.internal;

import java.io.Serializable;

/**
 * 带有主键的消息
 */
public interface IResultStatusMessage {

    enum Status{
        OK,FAIL,ERROR;
    }

    Status getStatus();

    Serializable getMessage();
}
