package com.codebroker.exception;

public class CCodecException extends CodeBrokerException {
	private static final long serialVersionUID = -9084607336239491702L;

	public CCodecException() {
	}

	public CCodecException(String message) {
		super(message);
	}

	public CCodecException(Throwable t) {
		super(t);
	}
}
