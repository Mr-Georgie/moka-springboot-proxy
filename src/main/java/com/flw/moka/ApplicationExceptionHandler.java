package com.flw.moka;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.flw.moka.entity.response.ProxyResponse;
import com.flw.moka.exception.BadCredentialsException;
import com.flw.moka.exception.InvalidProductRequestException;
import com.flw.moka.exception.NoMethodNamePassedException;
import com.flw.moka.exception.TransactionMethodAlreadyDoneException;
import com.flw.moka.exception.TransactionNotCapturedException;
import com.flw.moka.exception.TransactionNotFoundException;
import com.flw.moka.exception.TransactionShouldBeRefundedException;
import com.flw.moka.exception.TransactionShouldBeVoidedException;

@ControllerAdvice
public class ApplicationExceptionHandler extends ResponseEntityExceptionHandler {

    private ProxyResponse responseCreator() {
        ProxyResponse proxyResponse = new ProxyResponse();
        proxyResponse.setTransactionReference("N/A");
        proxyResponse.setExternalReference("N/A");
        proxyResponse.setProvider("MOKA");
        proxyResponse.setCode("RR");

        return proxyResponse;
    }

    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<Object> handleTransactionNotFoundException(TransactionNotFoundException ex) {
        ProxyResponse proxyResponse = responseCreator();
        proxyResponse.setMessage(ex.getMessage());
        return new ResponseEntity<>(proxyResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TransactionMethodAlreadyDoneException.class)
    protected ResponseEntity<Object> handleTransactionMethodAlreadyDoneException(
            TransactionMethodAlreadyDoneException ex) {
        ProxyResponse proxyResponse = responseCreator();
        proxyResponse.setMessage(ex.getMessage());
        return new ResponseEntity<>(proxyResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TransactionShouldBeVoidedException.class)
    protected ResponseEntity<Object> handleTransactionShouldBeVoidedException(TransactionShouldBeVoidedException ex) {
        ProxyResponse proxyResponse = responseCreator();
        proxyResponse.setMessage(ex.getMessage());
        return new ResponseEntity<>(proxyResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TransactionShouldBeRefundedException.class)
    protected ResponseEntity<Object> handleTransactionShouldBeRefundedException(
            TransactionShouldBeRefundedException ex) {
        ProxyResponse proxyResponse = responseCreator();
        proxyResponse.setMessage(ex.getMessage());
        return new ResponseEntity<>(proxyResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TransactionNotCapturedException.class)
    protected ResponseEntity<Object> handleTransactionNotCapturedException(TransactionNotCapturedException ex) {
        ProxyResponse proxyResponse = responseCreator();
        proxyResponse.setMessage(ex.getMessage());
        return new ResponseEntity<>(proxyResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoMethodNamePassedException.class)
    protected ResponseEntity<Object> handleNoMethodPassedException(NoMethodNamePassedException ex) {
        ProxyResponse proxyResponse = responseCreator();
        proxyResponse.setMessage(ex.getMessage());
        return new ResponseEntity<>(proxyResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidProductRequestException.class)
    protected ResponseEntity<Object> handleInvalidProductRequestException(InvalidProductRequestException ex) {
        ProxyResponse proxyResponse = responseCreator();
        proxyResponse.setMessage(ex.getMessage());
        return new ResponseEntity<>(proxyResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    protected ResponseEntity<Object> handleBadCredentialsException(BadCredentialsException ex) {
        ProxyResponse proxyResponse = responseCreator();
        proxyResponse.setMessage(ex.getMessage());
        return new ResponseEntity<>(proxyResponse, HttpStatus.BAD_REQUEST);
    }

}
