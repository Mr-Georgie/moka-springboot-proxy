package com.flw.moka.exception;

public class InvalidRefundRequestException extends RuntimeException {

    public InvalidRefundRequestException(String message) {
        super(message);
    }
}