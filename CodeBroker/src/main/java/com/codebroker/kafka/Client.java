package com.codebroker.kafka;

import java.io.File;
import java.util.concurrent.CompletionStage;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.Done;
import akka.actor.ActorSystem;
import akka.kafka.ProducerMessage;
import akka.kafka.ProducerSettings;
import akka.kafka.javadsl.Producer;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

public class Client {

	public static void main(String[] args) {
		File file = new File("D:\\Users\\xl\\workspace\\FlayShooting\\conf\\application.conf");
		Config cg = ConfigFactory.parseFile(file);
		cg.withFallback(ConfigFactory.defaultReference(Thread.currentThread().getContextClassLoader()));
		Config config = ConfigFactory.load(cg).getConfig("CodeBroker");
		ActorSystem system = ActorSystem.create("CodeBroker", config);
		ProducerSettings<String, String> producerSettings = ProducerSettings
				.create(system, new StringSerializer(), new StringSerializer())
				.withBootstrapServers("192.168.0.242:9092");
		final Materializer materializer = ActorMaterializer.create(system);

		CompletionStage<Done> done =
				  Source.range(300, 500)
				    .map(n -> {
				      //int partition = Math.abs(n) % 2;
				      int partition = 0;
				      String elem = String.valueOf(n);
				      return new ProducerMessage.Message<String, String, Integer>(
				        new ProducerRecord<>("topic1", partition, null, elem), n);
				    })
				    .via(Producer.flow(producerSettings))
				    .map(result -> {
				      ProducerRecord<String, String> record = result.message().record();
				      System.out.println(record);
				      return result;
				    })
				    .runWith(Sink.ignore(), materializer);
	}
}
