package com.codebroker.protocol;

import com.codebroker.api.internal.ByteArrayPacket;
import com.codebroker.core.data.CObject;
import com.codebroker.core.data.IObject;

import java.util.HashMap;
import java.util.Map;

public class SystemProtocolCodecFactory {

    private Map<Integer, ISystemProtocolCodec> systemProtocolCodecMap;

    public SystemProtocolCodecFactory() {
        this.systemProtocolCodecMap = new HashMap<Integer, ISystemProtocolCodec>();
    }

    /**
     * 系统拆包
     *
     * @param byteArrayPacket
     * @return
     */
    public IObject unpackByteArrayPacket(ByteArrayPacket byteArrayPacket) {
        int opCode = byteArrayPacket.getOpCode();
        if (systemProtocolCodecMap.containsKey(opCode)) {
            IObject iObject = systemProtocolCodecMap.get(opCode).unpackByteArrayPacket(byteArrayPacket);
            return iObject;
        }
        return CObject.newInstance();
    }


}
