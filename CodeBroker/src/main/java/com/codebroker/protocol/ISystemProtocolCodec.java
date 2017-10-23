package com.codebroker.protocol;

import com.codebroker.api.internal.ByteArrayPacket;
import com.codebroker.core.data.IObject;

/**
 * 系统内部协议拆包
 */
public interface ISystemProtocolCodec {
    /**
     * 解包
     *
     * @param byteArrayPacket
     * @return
     */
    public IObject unpackByteArrayPacket(ByteArrayPacket byteArrayPacket);

    /**
     * 打包
     *
     * @param object
     * @return
     */
    public ByteArrayPacket packMessage(Object object);

}
