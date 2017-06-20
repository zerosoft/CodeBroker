package com.codebroker.util;

import java.io.IOException;

import ch.qos.logback.core.encoder.EncoderBase;

public class LayoutWrappingEncoder<E> extends EncoderBase<E> {

	@Override
	public void doEncode(E event) throws IOException {
		System.out.println(event);
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub

	}

}
