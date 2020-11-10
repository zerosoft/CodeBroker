package com.codebroker.demo.pointtopoint;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.delivery.ProducerController;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.math.BigInteger;

public class FibonacciProducer extends AbstractBehavior<FibonacciProducer.Command> {

	private long n = 0;
	private BigInteger b = BigInteger.ONE;
	private BigInteger a = BigInteger.ZERO;

	public interface Command {}

	private static class WrappedRequestNext implements Command {
		final ProducerController.RequestNext<FibonacciConsumer.Command> next;

		private WrappedRequestNext(ProducerController.RequestNext<FibonacciConsumer.Command> next) {
			this.next = next;
		}
	}

	private FibonacciProducer(ActorContext<Command> context) {
		super(context);
	}

	public static Behavior<Command> create(
			ActorRef<ProducerController.Command<FibonacciConsumer.Command>> producerController) {
		return Behaviors.setup(
				context -> {
					ActorRef<ProducerController.RequestNext<FibonacciConsumer.Command>> requestNextAdapter =
							context.messageAdapter(
									ProducerController.requestNextClass(), WrappedRequestNext::new);
					producerController.tell(new ProducerController.Start<>(requestNextAdapter));

					return new FibonacciProducer(context);
				});
	}

	@Override
	public Receive<Command> createReceive() {
		return newReceiveBuilder()
				.onMessage(WrappedRequestNext.class, w -> onWrappedRequestNext(w))
				.build();
	}

	private Behavior<Command> onWrappedRequestNext(WrappedRequestNext w) {
		getContext().getLog().info("Generated fibonacci {}: {}", n, a);
		w.next.sendNextTo().tell(new FibonacciConsumer.FibonacciNumber(n, a));

		if (n == 1000) {
			return Behaviors.stopped();
		} else {
			n = n + 1;
			b = a.add(b);
			a = b;
			return this;
		}
	}
}