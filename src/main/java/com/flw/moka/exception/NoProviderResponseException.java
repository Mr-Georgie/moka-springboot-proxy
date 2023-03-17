package com.flw.moka.exception;

public class NoProviderResponseException extends RuntimeException {

    public NoProviderResponseException() {
        super("No response was gotten from the provider");
    }
}
