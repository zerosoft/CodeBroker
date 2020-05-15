package com.codebroker.api.internal;

import java.nio.ByteBuffer;

/**
 * 网络封包 获取操作码及数据内容.
 *
 *  @author LongJu
 */
public interface ByteArrayPacket extends IPacket{


    /**
     * * 获得元数据.
     *
     * @return the raw data
     */
    byte[] getRawData();

    /**
     * 获得序列化后的
     *
     * @return
     */
    byte[] toBinary();

    /**
     * 序列化返回对象
     *
     * @param binary
     */
    void fromBinary(byte[] binary);

    /**
     * 获得序列化后的
     *
     * @return
     */
    ByteBuffer toByteBuffer();

    /**
     * 序列化返回对象
     *
     * @param buffer
     */
    void fromBuffer(ByteBuffer buffer);

}
