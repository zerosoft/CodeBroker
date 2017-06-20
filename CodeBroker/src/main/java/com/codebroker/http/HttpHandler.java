package com.codebroker.http;

import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;

public interface HttpHandler {

	public String getUrlPath();

	public HttpResponse tet(HttpRequest request);

}
