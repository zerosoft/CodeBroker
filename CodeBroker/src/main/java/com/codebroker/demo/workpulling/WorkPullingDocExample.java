package com.codebroker.demo.workpulling;

/*
 * Copyright (C) 2020 Lightbend Inc. <https://www.lightbend.com>
 */


// #imports
import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.delivery.ConsumerController;
import akka.actor.typed.delivery.DurableProducerQueue;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

// #imports

// #producer
import akka.actor.typed.delivery.WorkPullingProducerController;
import akka.Done;

// #producer

// #durable-queue
import akka.persistence.typed.PersistenceId;
import akka.persistence.typed.delivery.EventSourcedProducerQueue;

// #durable-queue

import akka.actor.typed.javadsl.StashBuffer;
import akka.actor.typed.receptionist.ServiceKey;

public class WorkPullingDocExample {

	public static void main(String[] args) {
		ActorSystem<ImageWorkManager.Command> im = ActorSystem.create(ImageWorkManager.create(), "IM");

	}
}