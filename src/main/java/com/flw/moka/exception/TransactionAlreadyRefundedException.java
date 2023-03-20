package com.flw.moka.exception;

public class TransactionAlreadyRefundedException extends RuntimeException {
    public TransactionAlreadyRefundedException(String transactionRef) {
        super("The transaction with reference: '" + transactionRef + "' has already been refunded");
    }
}
