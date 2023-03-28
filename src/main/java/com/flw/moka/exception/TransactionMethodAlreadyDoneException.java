package com.flw.moka.exception;

public class TransactionMethodAlreadyDoneException extends RuntimeException {

    public TransactionMethodAlreadyDoneException(String message) {
        super(message);
    }
}
