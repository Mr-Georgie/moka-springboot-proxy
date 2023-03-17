package com.flw.moka.exception;

public class TransactionAlreadyVoidedException extends RuntimeException {

    public TransactionAlreadyVoidedException(String transactionRef) {
        super("The transaction with reference: '" + transactionRef + "' has already been voided");
    }
}
