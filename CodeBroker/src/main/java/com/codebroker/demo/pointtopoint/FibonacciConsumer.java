package com.codebroker.demo.pointtopoint;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.delivery.ConsumerController;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.math.BigInteger;

public class FibonacciConsumer extends AbstractBehavior<FibonacciConsumer.Command> {

	public interface Command {}

	public static class FibonacciNumber implements Command {
		public final long n;
		public final BigInteger value;

		public FibonacciNumber(long n, BigInteger value) {
			this.n = n;
			this.value = value;
		}
	}

	private static class WrappedDelivery implements Command {
		final ConsumerController.Delivery<Command> delivery;

		private WrappedDelivery(ConsumerController.Delivery<Command> delivery) {
			this.delivery = delivery;
		}
	}

	public static Behavior<Command> create(
			ActorRef<ConsumerController.Command<Command>> consumerController) {
		return Behaviors.setup(
				context -> {
					ActorRef<ConsumerController.Delivery<Command>> deliveryAdapter =
							context.messageAdapter(ConsumerController.deliveryClass(), WrappedDelivery::new);
					consumerController.tell(new ConsumerController.Start<>(deliveryAdapter));

					return new FibonacciConsumer(context);
				});
	}

	private FibonacciConsumer(ActorContext<Command> context) {
		super(context);
	}

	@Override
	public Receive<Command> createReceive() {
		return newReceiveBuilder().onMessage(WrappedDelivery.class, this::onDelivery).build();
	}

	private Behavior<Command> onDelivery(WrappedDelivery w) {
		FibonacciNumber number = (FibonacciNumber) w.delivery.message();
		getContext().getLog().info("Processed fibonacci {}: {}", number.n, number.value);
		w.delivery.confirmTo().tell(ConsumerController.confirmed());
		return this;
	}
}