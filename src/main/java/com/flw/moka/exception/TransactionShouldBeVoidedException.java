package com.flw.moka.exception;

public class TransactionShouldBeVoidedException extends RuntimeException {

    public TransactionShouldBeVoidedException(String message) {
        super(message);
    }

}
