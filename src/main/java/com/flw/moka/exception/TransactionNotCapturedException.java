package com.flw.moka.exception;

public class TransactionNotCapturedException extends RuntimeException {

    public TransactionNotCapturedException(String message) {
        super(message);
    }

}
