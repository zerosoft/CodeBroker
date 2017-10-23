package com.codebroker.kafka;

import akka.Done;
import akka.actor.ActorSystem;
import akka.kafka.ConsumerSettings;
import akka.kafka.Subscriptions;
import akka.kafka.javadsl.Consumer;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Sink;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicLong;

public class Server {
    public static void main(String[] args) {
        File file = new File("D:\\Users\\xl\\workspace\\FlayShooting\\conf\\application.conf");
        Config cg = ConfigFactory.parseFile(file);
        cg.withFallback(ConfigFactory.defaultReference(Thread.currentThread().getContextClassLoader()));
        Config config = ConfigFactory.load(cg).getConfig("CodeBroker");
        ActorSystem system = ActorSystem.create("CodeBroker", config);
        final Materializer materializer = ActorMaterializer.create(system);

        final ConsumerSettings<String, String> consumerSettings = ConsumerSettings
                .create(system, new StringDeserializer(), new StringDeserializer())
                .withBootstrapServers("192.168.0.242:9092").withGroupId("group1")
                .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        final DB db = new DB();

        Consumer.committableSource(consumerSettings, Subscriptions.topics("topic1"))
                .mapAsync(1, msg -> db.update(msg.record().value()).thenApply(done -> msg))
                .mapAsync(1, msg -> msg.committableOffset().commitJavadsl()).runWith(Sink.ignore(), materializer);
    }

    static class DB {
        private final AtomicLong offset = new AtomicLong();

        public CompletionStage<Done> save(ConsumerRecord<String, String> record) {
            System.out.println("DB.save=========: " + record.value());
            offset.set(record.offset());
            return CompletableFuture.completedFuture(Done.getInstance());
        }

        public CompletionStage<Long> loadOffset() {
            return CompletableFuture.completedFuture(offset.get());
        }

        public CompletionStage<Done> update(String data) {
            System.out.println("DB.update========: " + data);
            return CompletableFuture.completedFuture(Done.getInstance());
        }
    }

}
