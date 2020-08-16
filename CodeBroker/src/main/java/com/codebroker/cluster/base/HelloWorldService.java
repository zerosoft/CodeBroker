package com.codebroker.cluster.base;

import akka.actor.typed.ActorSystem;
import akka.cluster.sharding.typed.javadsl.ClusterSharding;
import akka.cluster.sharding.typed.javadsl.EntityRef;
import akka.cluster.sharding.typed.javadsl.Entity;
import akka.persistence.typed.PersistenceId;

import java.time.Duration;
import java.util.concurrent.CompletionStage;

public  class HelloWorldService {
    private final ActorSystem<?> system;
    private final ClusterSharding sharding;
    private final Duration askTimeout = Duration.ofSeconds(5);

    // registration at startup
    public HelloWorldService(ActorSystem<?> system) {
        this.system = system;
        sharding = ClusterSharding.get(system);

        // registration at startup
        sharding.init(
                Entity.of(
                        HelloWorld.ENTITY_TYPE_KEY,
                        entityContext ->
                                HelloWorld.create(
                                        entityContext.getEntityId(),
                                        PersistenceId.of(
                                                entityContext.getEntityTypeKey().name(), entityContext.getEntityId()))));
    }

    // usage example
    public CompletionStage<Integer> sayHello(String worldId, String whom) {
        EntityRef<HelloWorld.Command> entityRef =
                sharding.entityRefFor(HelloWorld.ENTITY_TYPE_KEY, worldId);
        CompletionStage<HelloWorld.Greeting> result =
                entityRef.ask(replyTo -> new HelloWorld.Greet(whom, replyTo), askTimeout);
        return result.thenApply(greeting -> greeting.numberOfPeople);
    }
}