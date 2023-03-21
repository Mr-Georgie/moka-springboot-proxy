package com.flw.moka.exception;

public class TransactionMethodAlreadyDoneException extends RuntimeException {

    public TransactionMethodAlreadyDoneException(String transactionRef, String method) {
        super("Transaction with ref: " + transactionRef + " has already been " + method.toUpperCase());
    }
}
