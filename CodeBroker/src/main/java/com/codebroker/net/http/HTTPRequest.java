package com.codebroker.net.http;

import akka.http.javadsl.marshallers.jackson.Jackson;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HTTPRequest {
	public final String serviceName;
	public final String message;

	public HTTPRequest(String serviceName,String message) {
		this.serviceName = serviceName;
		this.message=message;
	}

	public static void main(String[] args) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		HTTPRequest httpRequest=new HTTPRequest("123","esrs");
		// 写为字符串
		String text = mapper.writeValueAsString(httpRequest);
		// 写为文件
		System.out.println(text);
	}
}
