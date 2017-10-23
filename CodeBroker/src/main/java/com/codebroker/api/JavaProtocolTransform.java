package com.codebroker.api;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

public interface JavaProtocolTransform {

    public void protocolToJavaBean(Message message);

    public Message javaBeanToProtocol();

    public byte[] getByteArray();

    public Message bytesToProtocol(byte[] bytes) throws InvalidProtocolBufferException;
}
