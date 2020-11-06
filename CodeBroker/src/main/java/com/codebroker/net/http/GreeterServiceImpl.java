//package com.codebroker.net.http;
//
////#import
//
//import akka.NotUsed;
//import akka.actor.typed.ActorSystem;
//import akka.japi.Pair;
//import akka.stream.Materializer;
//import akka.stream.javadsl.*;
//import com.codebroker.grpc.GreeterService;
//import com.codebroker.grpc.HelloReply;
//import com.codebroker.grpc.HelloRequest;
//
//import java.util.List;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.CompletionStage;
//import java.util.stream.Collectors;
//
////#import
//
////#service-request-reply
////#service-stream
//class GreeterServiceImpl implements GreeterService {
//  final ActorSystem<?> system;
//  //#service-request-reply
//  final Sink<HelloRequest, NotUsed> inboundHub;
//  final Source<HelloReply, NotUsed> outboundHub;
//  //#service-request-reply
//
//  public GreeterServiceImpl(ActorSystem<?> system) {
//    this.system = system;
//    //#service-request-reply
//    Pair<Sink<HelloRequest, NotUsed>, Source<HelloReply, NotUsed>> hubInAndOut =
//            MergeHub.of(HelloRequest.class)
//                    .map(request ->
//                            HelloReply.newBuilder()
//                                    .setMessage("Hello, " + request.getName())
//                                    .build())
//                    .toMat(BroadcastHub.of(HelloReply.class), Keep.both())
//                    .run(system);
//
//    inboundHub = hubInAndOut.first();
//    outboundHub = hubInAndOut.second();
//    //#service-request-reply
//  }
//
//  @Override
//  public CompletionStage<HelloReply> sayHello(HelloRequest request) {
//    return CompletableFuture.completedFuture(
//            HelloReply.newBuilder()
//                    .setMessage("Hello, " + request.getName())
//                    .build()
//    );
//  }
//
//  @Override
//  public CompletionStage<HelloReply> itKeepsTalking(Source<HelloRequest, NotUsed> request) {
//    return null;
//  }
//
//  @Override
//  public Source<HelloReply, NotUsed> itKeepsReplying(HelloRequest request) {
//    return null;
//  }
//
//  @Override
//  public Source<HelloReply, NotUsed> streamHellos(Source<HelloRequest, NotUsed> request) {
//    return null;
//  }
//
////  @Override
////  public CompletionStage<HelloReply> sayHello(HelloRequest in) {
////    System.out.println("sayHello to " + in.getName());
////    HelloReply reply = HelloReply.newBuilder().setMessage("Hello, " + in.getName()).build();
////    return CompletableFuture.completedFuture(reply);
////  }
////
////  @Override
////  public CompletionStage<HelloReply> itKeepsTalking(Source<HelloRequest, NotUsed> in) {
////    System.out.println("sayHello to in stream...");
////    return in.runWith(Sink.seq(), mat)
////            .thenApply(elements -> {
////              String elementsStr = elements.stream().map(elem -> elem.getName())
////                      .collect(Collectors.toList()).toString();
////              return HelloReply.newBuilder().setMessage("Hello, " + elementsStr).build();
////            });
////  }
////
////  @Override
////  public Source<HelloReply, NotUsed> itKeepsReplying(HelloRequest in) {
////    System.out.println("sayHello to " + in.getName() + " with stream of chars");
////    List<Character> characters = ("Hello, " + in.getName())
////            .chars().mapToObj(c -> (char) c).collect(Collectors.toList());
////    return Source.from(characters)
////            .map(character -> {
////              return HelloReply.newBuilder().setMessage(String.valueOf(character)).build();
////            });
////  }
////
////  @Override
////  public Source<HelloReply, NotUsed> streamHellos(Source<HelloRequest, NotUsed> in) {
////    System.out.println("sayHello to stream...");
////    return in.map(request -> HelloReply.newBuilder().setMessage("Hello, " + request.getName()).build());
////  }
//}
////#service-stream
////#service-request-reply
