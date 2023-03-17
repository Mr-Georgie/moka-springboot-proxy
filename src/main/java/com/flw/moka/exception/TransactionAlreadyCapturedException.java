package com.flw.moka.exception;

public class TransactionAlreadyCapturedException extends RuntimeException {

    public TransactionAlreadyCapturedException(String transactionRef) {
        super("The transaction with reference: '" + transactionRef + "' has already been captured");
    }
}
