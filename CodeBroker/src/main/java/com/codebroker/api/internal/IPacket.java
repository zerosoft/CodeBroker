package com.codebroker.api.internal;

import com.codebroker.protocol.BaseByteArrayPacket;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
		use = JsonTypeInfo.Id.NAME,
		property = "type"
)
@JsonSubTypes({
		@JsonSubTypes.Type(value = BaseByteArrayPacket.class, name = "BaseByteArrayPacket"),
		@JsonSubTypes.Type(value = ByteArrayPacket.class, name = "ByteArrayPacket")
})
public interface IPacket {
	/**
	 * 获取操作码.
	 *
	 * @return the op code
	 */
	int getOpCode();

	/**
	 * 获得消息体内容
	 * @return
	 */
	Object getRawData();
}
