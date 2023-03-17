package com.flw.moka.exception;

public class NoProviderResponseDataException extends RuntimeException {

    public NoProviderResponseDataException() {
        super("No data was found in provider response was gotten from the provider");
    }
}
