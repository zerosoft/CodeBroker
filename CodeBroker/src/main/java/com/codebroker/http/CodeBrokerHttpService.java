package com.codebroker.http;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.AllDirectives;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;

/**
 * foo://example.com:8042/over/there?name=ferret#nose \_/
 * \______________/\_________/ \_________/ \__/ | | | | | scheme authority path
 * query fragment | _____________________|__ / \ / \
 * urn:example:animal:ferret:nose
 *
 * @author xl
 */
public class CodeBrokerHttpService extends AllDirectives {

    public CodeBrokerHttpService() {
        super();
        final ActorSystem system = ActorSystem.create("SimpleServerHttpHttpsApp");
        final ActorMaterializer materializer = ActorMaterializer.create(system);

        final SimpleServerApp app = new SimpleServerApp();
        final Flow<HttpRequest, HttpResponse, NotUsed> flow = app.createRoute().flow(system, materializer);

        // #both-https-and-http
        final Http http = Http.get(system);
        // Run HTTP server firstly
        http.bindAndHandle(flow, ConnectHttp.toHost("localhost", 80), materializer);

        // get configured HTTPS context

        // sets default context to HTTPS – all Http() bound servers for this
        // ActorSystem will use HTTPS from now on
        // Then run HTTPS server
        http.bindAndHandle(flow, ConnectHttp.toHost("localhost", 443), materializer);
        // #both-https-and-http

        System.out.println("Type RETURN to exit");
        system.terminate();
    }

}
