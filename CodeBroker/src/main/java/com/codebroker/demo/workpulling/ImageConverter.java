package com.codebroker.demo.workpulling;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.delivery.ConsumerController;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.receptionist.ServiceKey;

import java.util.UUID;

public class ImageConverter {
	interface Command {}

	public static class ConversionJob {
		public final UUID resultId;
		public final String fromFormat;
		public final String toFormat;
		public final byte[] image;

		public ConversionJob(UUID resultId, String fromFormat, String toFormat, byte[] image) {
			this.resultId = resultId;
			this.fromFormat = fromFormat;
			this.toFormat = toFormat;
			this.image = image;
		}
	}

	private static class WrappedDelivery implements Command {
		final ConsumerController.Delivery<ConversionJob> delivery;

		private WrappedDelivery(ConsumerController.Delivery<ConversionJob> delivery) {
			this.delivery = delivery;
		}
	}

	public static ServiceKey<ConsumerController.Command<ConversionJob>> serviceKey =
			ServiceKey.create(ConsumerController.serviceKeyClass(), "ImageConverter");

	public static Behavior<Command> create() {
		return Behaviors.setup(
				context -> {
					ActorRef<ConsumerController.Delivery<ConversionJob>> deliveryAdapter =
							context.messageAdapter(ConsumerController.deliveryClass(), WrappedDelivery::new);
					ActorRef<ConsumerController.Command<ConversionJob>> consumerController =
							context.spawn(ConsumerController.create(serviceKey), "consumerController");
					consumerController.tell(new ConsumerController.Start<>(deliveryAdapter));

					return Behaviors.receive(Command.class)
							.onMessage(WrappedDelivery.class, ImageConverter::onDelivery)
							.build();
				});
	}

	private static Behavior<Command> onDelivery(WrappedDelivery w) {
		byte[] image = w.delivery.message().image;
		String fromFormat = w.delivery.message().fromFormat;
		String toFormat = w.delivery.message().toFormat;
		// convert image...
		// store result with resultId key for later retrieval

		// and when completed confirm
		w.delivery.confirmTo().tell(ConsumerController.confirmed());

		return Behaviors.same();
	}
}