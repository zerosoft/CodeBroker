package com.codebroker.net.http;


public class HTTPRequest {
	public final String serviceName;
	public final String message;

	public HTTPRequest(String serviceName,String message) {
		this.serviceName = serviceName;
		this.message=message;
	}

}
