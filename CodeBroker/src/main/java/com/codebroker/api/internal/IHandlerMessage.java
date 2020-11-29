package com.codebroker.api.internal;

public interface  IHandlerMessage<T,P> {

    /**
     * 处理消息.
     *
     * @param obj the obj
     */
   void handleMessage(T obj);

    /**
     * 处理带有同步的返回消息
     * @param obj
     * @return
     */
    P handleBackMessage(T obj);


}
