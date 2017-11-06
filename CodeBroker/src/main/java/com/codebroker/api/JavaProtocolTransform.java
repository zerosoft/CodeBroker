package com.codebroker.api;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

public interface JavaProtocolTransform {

     void protocolToJavaBean(Message message);


     Message javaBeanToProtocol();

     byte[] getByteArray();

     Message bytesToProtocol(byte[] bytes) throws InvalidProtocolBufferException;
}
