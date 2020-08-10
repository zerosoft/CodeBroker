package com.codebroker.net.http;

import akka.http.javadsl.HandlerProvider;
import akka.http.javadsl.server.AllDirectives;

public class HttpDirectives extends AllDirectives {
    public HandlerProvider createRoute() {
        return concat(
                path("hello", () ->
                        get(() ->
                                complete("<h1>Say hello to akka-http</h1>"))));
    }
}
