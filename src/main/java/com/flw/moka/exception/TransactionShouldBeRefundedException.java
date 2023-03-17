package com.flw.moka.exception;

public class TransactionShouldBeRefundedException extends RuntimeException {

    public TransactionShouldBeRefundedException(String message) {
        super(message);
    }

}
