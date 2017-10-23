package com.codebroker.protocol;

import com.codebroker.api.internal.ByteArrayPacket;
import com.codebroker.core.data.CObject;
import com.codebroker.core.data.IObject;
import com.codebroker.setting.SystemRequest;


public class JSONSystemProtocolCodec implements ISystemProtocolCodec {
    @Override
    public IObject unpackByteArrayPacket(ByteArrayPacket byteArrayPacket) {
        int opCode = byteArrayPacket.getOpCode();

        switch (SystemRequest.get(opCode)) {
            case USER_LOGIN_JSON:
                String para = new String(byteArrayPacket.getRawData());
                IObject cObject = CObject.newFromJsonData(para);
                return cObject;
            default:
                break;
        }
        return CObject.newInstance();
    }

    @Override
    public ByteArrayPacket packMessage(Object object) {
        return null;
    }
}
