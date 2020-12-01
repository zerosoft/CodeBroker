package com.codebroker.protocol;

import akka.actor.ExtendedActorSystem;
import com.codebroker.core.ServerEngine;
import io.altoo.akka.serialization.kryo.DefaultKryoInitializer;
import io.altoo.akka.serialization.kryo.serializer.scala.ScalaKryo;

public class InitKryoInitializer extends DefaultKryoInitializer {
	@Override
	public void initAkkaSerializer(ScalaKryo kryo,  ExtendedActorSystem system) {
		super.initAkkaSerializer(kryo, system);
		ClassLoader classLoader = ServerEngine.getInstance().getiClassLoader();
		kryo.setClassLoader(classLoader);
	}
}
