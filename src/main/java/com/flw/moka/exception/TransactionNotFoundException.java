package com.flw.moka.exception;

public class TransactionNotFoundException extends RuntimeException {

    public TransactionNotFoundException(String transactionRef) {
        super("The transaction with reference: '" + transactionRef + "' does not exist in our records");
    }
}
