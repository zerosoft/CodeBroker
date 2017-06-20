package com.codebroker.exception;

public class CRuntimeException extends CodeBrokerException {

	private static final long serialVersionUID = -4368942506552917954L;

	public CRuntimeException() {
	}

	public CRuntimeException(String message) {
		super(message);
	}

	public CRuntimeException(Throwable t) {
		super(t);
	}

}
