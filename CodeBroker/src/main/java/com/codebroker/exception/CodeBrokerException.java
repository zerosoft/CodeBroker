package com.codebroker.exception;

public class CodeBrokerException extends RuntimeException {

	public CodeBrokerException() {
		super();
	}

	public CodeBrokerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CodeBrokerException(String message, Throwable cause) {
		super(message, cause);
	}

	public CodeBrokerException(Throwable cause) {
		super(cause);
	}

	public CodeBrokerException(String message) {
		super(message);
	}

}
