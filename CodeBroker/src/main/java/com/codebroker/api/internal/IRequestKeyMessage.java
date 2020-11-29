package com.codebroker.api.internal;

import java.io.Serializable;

/**
 * 带有主键的消息
 */
public interface IRequestKeyMessage<T> {

    T getKey();

    Serializable message();
}
