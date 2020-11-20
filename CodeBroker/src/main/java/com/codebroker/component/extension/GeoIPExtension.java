package com.codebroker.component.extension;


import akka.actor.typed.ActorSystem;
import akka.actor.typed.Extension;
import akka.actor.typed.ExtensionId;
import akka.actor.typed.javadsl.Behaviors;
import com.codebroker.component.service.GeoIPComponent;
import com.codebroker.core.ContextResolver;

import java.util.Optional;

public class GeoIPExtension implements Extension {
	Optional<GeoIPComponent> manager;

	public GeoIPExtension() {
		manager = ContextResolver.getManager(GeoIPComponent.class);
	}



	static class  Id extends ExtensionId<GeoIPExtension>{
		private static final Id instance = new Id();

		private Id() {}
		@Override
		public GeoIPExtension createExtension(ActorSystem<?> system) {
			return new GeoIPExtension();
		}

		public static GeoIPExtension get(ActorSystem<?> system) {
			return instance.apply(system);
		}

		public void test(){
			Behaviors.setup(
					(context) -> {
						Id.get(context.getSystem())
								.manager.get().getCityCountry("sss");
						return Behaviors.same();
					});
		}
	}
}


