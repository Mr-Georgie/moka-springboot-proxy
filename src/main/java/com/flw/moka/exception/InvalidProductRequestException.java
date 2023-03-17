package com.flw.moka.exception;

import java.util.ArrayList;
import java.util.List;

import org.springframework.validation.BindingResult;

public class InvalidProductRequestException extends RuntimeException {

    public InvalidProductRequestException(BindingResult bindingResult) {
        super(getAllErrors(bindingResult));
    }

    private static String getAllErrors(BindingResult bindingResult) {
        List<String> errors = new ArrayList<>();
        bindingResult.getAllErrors().forEach((error) -> errors.add(error.getDefaultMessage()));
        return errors.toString();
    }
}
