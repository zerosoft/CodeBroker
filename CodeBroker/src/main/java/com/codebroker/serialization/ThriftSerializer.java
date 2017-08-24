package com.codebroker.serialization;

import org.apache.thrift.TBase;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;

import com.message.thrift.actor.ActorMessage;

import akka.serialization.JSerializer;
import akka.serialization.SerializerWithStringManifest;

public class ThriftSerializer  extends SerializerWithStringManifest {

	public final String ActorMessage="AM";
	public final TSerializer serializer=new TSerializer();
	public final TDeserializer dserializer=new TDeserializer();
	
	@Override
	public Object fromBinary(byte[] bytes, String classname) {
		try {
			dserializer.deserialize(new ActorMessage(), bytes);
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public int identifier() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String manifest(Object obj) {
		if (obj instanceof ActorMessage) {
			return ActorMessage;
		}
		return null;
	}

	@Override
	public byte[] toBinary(Object obj) {
		if (obj instanceof TBase) {
			if (obj instanceof ActorMessage) {
				try {
					byte[] serialize = serializer.serialize((TBase) obj);
				} catch (TException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return null;
	}


}
