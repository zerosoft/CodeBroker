package com.codebroker.util;

import ch.qos.logback.core.encoder.EncoderBase;

import java.io.IOException;

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
