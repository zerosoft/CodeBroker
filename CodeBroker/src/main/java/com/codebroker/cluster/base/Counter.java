package com.codebroker.cluster.base;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class Counter extends AbstractBehavior<Counter.Command> {

    public interface Command extends CborSerializable {}

    public enum Increment implements Command {
        INSTANCE
    }

    public static class GetValue implements Command {
        private final ActorRef<Integer> replyTo;

        public GetValue(ActorRef<Integer> replyTo) {
            this.replyTo = replyTo;
        }
    }

    public static Behavior<Command> create(String entityId) {
        return Behaviors.setup(context -> new Counter(context, entityId));
    }

    private final String entityId;
    private int value = 0;

    private Counter(ActorContext<Command> context, String entityId) {
        super(context);
        this.entityId = entityId;
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(Increment.class, msg -> onIncrement())
                .onMessage(GetValue.class, this::onGetValue)
                .build();
    }

    private Behavior<Command> onIncrement() {
        value++;
        return this;
    }

    private Behavior<Command> onGetValue(GetValue msg) {
        msg.replyTo.tell(value);
        return this;
    }
}