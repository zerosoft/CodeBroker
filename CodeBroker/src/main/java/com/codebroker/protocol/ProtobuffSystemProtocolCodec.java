package com.codebroker.protocol;

import com.codebroker.api.internal.ByteArrayPacket;
import com.codebroker.core.data.CObject;
import com.codebroker.core.data.IObject;
import com.codebroker.setting.SystemRequest;
import com.google.protobuf.InvalidProtocolBufferException;
import com.message.protocol.PBSystem;


public class ProtobuffSystemProtocolCodec implements ISystemProtocolCodec {
    @Override
    public IObject unpackByteArrayPacket(ByteArrayPacket byteArrayPacket) {
        int opCode = byteArrayPacket.getOpCode();
        IObject cObject = CObject.newInstance();
        switch (SystemRequest.get(opCode)) {
            case USER_LOGIN_PB:
                try {
                    PBSystem.CS_USER_CONNECT_TO_SERVER login = PBSystem.CS_USER_CONNECT_TO_SERVER.parseFrom(byteArrayPacket.getRawData());
                    String name = login.getName();
                    String params = login.getParams();
                    cObject.putUtfString("name", name);
                    cObject.putUtfString("params", params);
                    return cObject;
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
        return cObject;
    }

    @Override
    public ByteArrayPacket packMessage(Object object) {
        return null;
    }
}
