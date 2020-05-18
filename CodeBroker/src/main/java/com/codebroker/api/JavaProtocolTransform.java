package com.codebroker.api;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

/**
 * Proto buffer和java 对象之间的转换
 */
public interface JavaProtocolTransform {

     void protocolToJavaBean(Message message);

     Message javaBeanToProtocol();

     byte[] getByteArray();

     Message bytesToProtocol(byte[] bytes) throws InvalidProtocolBufferException;
}
