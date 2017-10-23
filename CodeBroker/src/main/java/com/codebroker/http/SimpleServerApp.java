package com.codebroker.http;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpEntity;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.Multipart.FormData;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.http.javadsl.unmarshalling.Unmarshaller;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;

import java.io.IOException;

import static akka.http.javadsl.unmarshalling.Unmarshaller.entityToString;

public class SimpleServerApp extends AllDirectives { // or import Directives.*

    public static void main(String[] args) throws IOException {
        final ActorSystem system = ActorSystem.create("SimpleServerApp");
        final ActorMaterializer materializer = ActorMaterializer.create(system);

        final SimpleServerApp app = new SimpleServerApp();
        final Flow<HttpRequest, HttpResponse, NotUsed> flow = app.createRoute().flow(system, materializer);

        Http.get(system).bindAndHandle(flow, ConnectHttp.toHost("localhost", 8080), materializer);

        System.out.println("Type RETURN to exit");
        System.in.read();
        system.terminate();
    }

    public Route createRoute() {
        return route(path("hello", () -> get(() -> complete("<h1>Say hello to akka-http</h1>"))),
                post(() -> path("hello", () -> {
                    Unmarshaller<HttpEntity, String> entityToString = entityToString();
                    String string = entityToString.toString();
                    Unmarshaller<HttpEntity, FormData> entityToMultipartFormData = entityToString
                            .entityToMultipartFormData();
                    return entity(entityToString, body -> complete("Hello " + body + "!"));
                })));
    }
    // #https-http-app

}
