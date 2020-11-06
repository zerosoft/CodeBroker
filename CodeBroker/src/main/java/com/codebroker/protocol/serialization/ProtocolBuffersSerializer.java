package com.codebroker.protocol.serialization;

import akka.actor.ExtendedActorSystem;
import akka.cluster.ddata.protobuf.AbstractSerializationSupport;
import akka.cluster.ddata.protobuf.ReplicatedDataSerializer;
import com.google.protobuf.GeneratedMessageV3;

public class ProtocolBuffersSerializer extends AbstractSerializationSupport {
	private final ExtendedActorSystem system;
	private final ReplicatedDataSerializer replicatedDataSerializer;

	public ProtocolBuffersSerializer(ExtendedActorSystem system) {
		this.system = system;
		this.replicatedDataSerializer = new ReplicatedDataSerializer(system);
	}

	@Override
	public ExtendedActorSystem system() {
		return this.system;
	}

	@Override
	public boolean includeManifest() {
		return false;
	}

	@Override
	public int identifier() {
		return 335453;
	}
	@Override
	public Object fromBinaryJava(byte[] bytes,  Class<?> manifest) {
		boolean assignableFrom = manifest.isAssignableFrom(GeneratedMessageV3.class);
		return null;
	}


	@Override
	public byte[] toBinary(Object o) {
		return new byte[0];
	}


}
