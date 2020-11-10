package com.codebroker.demo.pointtopoint;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.delivery.ConsumerController;
import akka.actor.typed.delivery.ProducerController;
import akka.actor.typed.javadsl.Behaviors;

import java.util.Optional;
import java.util.UUID;

public class Guardian {
	public static Behavior<Void> create() {
		return Behaviors.setup(
				context -> {
					// #connect
					ActorRef<ConsumerController.Command<FibonacciConsumer.Command>> consumerController =
							context.spawn(ConsumerController.create(), "consumerController");
					context.spawn(FibonacciConsumer.create(consumerController), "consumer");

					String producerId = "fibonacci-" + UUID.randomUUID();
					ActorRef<ProducerController.Command<FibonacciConsumer.Command>> producerController =
							context.spawn(
									ProducerController.create(
											FibonacciConsumer.Command.class, producerId, Optional.empty()),
									"producerController");
					context.spawn(FibonacciProducer.create(producerController), "producer");

					consumerController.tell(
							new ConsumerController.RegisterToProducerController<>(producerController));
					// #connect

					return Behaviors.empty();
				});
	}
}
