package com.codebroker.protocol.serialization;

import com.codebroker.core.data.CArray;
import com.codebroker.core.data.IArray;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class IArraySerializer extends Serializer<IArray> {

	private static final ThreadLocal<IArraySerializer> kryoLocal = ThreadLocal.withInitial(() -> {
		IArraySerializer iArraySerializer=new IArraySerializer();
		return iArraySerializer;
	});

	public static IArraySerializer getInstance() {
		return kryoLocal.get();
	}

	@Override
	public void write(Kryo kryo, Output output, IArray object) {
		byte[] bytes = object.toBinary();
		output.writeInt(bytes.length);
		output.write(bytes);
	}

	@Override
	public IArray read(Kryo kryo, Input input, Class type) {
		int length = input.readInt();
		byte[] bytes = input.readBytes(length);
		IArray cObject = CArray.newFromBinaryData(bytes);
		return cObject;
	}
}
