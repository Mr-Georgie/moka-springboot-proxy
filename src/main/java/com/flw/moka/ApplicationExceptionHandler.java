package com.flw.moka;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.flw.moka.entity.response.Meta;
import com.flw.moka.entity.response.ProxyResponse;
import com.flw.moka.exception.BadCredentialsException;
import com.flw.moka.exception.InvalidMethodNamePassedException;
import com.flw.moka.exception.InvalidProductRequestException;
import com.flw.moka.exception.TransactionMethodAlreadyDoneException;
import com.flw.moka.exception.TransactionNotCapturedException;
import com.flw.moka.exception.TransactionNotFoundException;
import com.flw.moka.exception.TransactionShouldBeRefundedException;
import com.flw.moka.exception.TransactionShouldBeVoidedException;

@ControllerAdvice
public class ApplicationExceptionHandler extends ResponseEntityExceptionHandler {

    private ProxyResponse responseCreator() {
        Meta meta = new Meta();
        ProxyResponse proxyResponse = new ProxyResponse();

        meta.setProvider("MOKA");
        proxyResponse.setResponseCode("RR");
        proxyResponse.setMeta(meta);
        return proxyResponse;
    }

    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<Object> handleTransactionNotFoundException(TransactionNotFoundException ex) {
        ProxyResponse proxyResponse = responseCreator();
        proxyResponse.setResponseMessage(ex.getMessage());
        return new ResponseEntity<>(proxyResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TransactionMethodAlreadyDoneException.class)
    protected ResponseEntity<Object> handleTransactionMethodAlreadyDoneException(
            TransactionMethodAlreadyDoneException ex) {
        ProxyResponse proxyResponse = responseCreator();
        proxyResponse.setResponseMessage(ex.getMessage());
        return new ResponseEntity<>(proxyResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TransactionShouldBeVoidedException.class)
    protected ResponseEntity<Object> handleTransactionShouldBeVoidedException(TransactionShouldBeVoidedException ex) {
        ProxyResponse proxyResponse = responseCreator();
        proxyResponse.setResponseMessage(ex.getMessage());
        return new ResponseEntity<>(proxyResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TransactionShouldBeRefundedException.class)
    protected ResponseEntity<Object> handleTransactionShouldBeRefundedException(
            TransactionShouldBeRefundedException ex) {
        ProxyResponse proxyResponse = responseCreator();
        proxyResponse.setResponseMessage(ex.getMessage());
        return new ResponseEntity<>(proxyResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TransactionNotCapturedException.class)
    protected ResponseEntity<Object> handleTransactionNotCapturedException(TransactionNotCapturedException ex) {
        ProxyResponse proxyResponse = responseCreator();
        proxyResponse.setResponseMessage(ex.getMessage());
        return new ResponseEntity<>(proxyResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidMethodNamePassedException.class)
    protected ResponseEntity<Object> handleNoMethodPassedException(InvalidMethodNamePassedException ex) {
        ProxyResponse proxyResponse = responseCreator();
        proxyResponse.setResponseMessage(ex.getMessage());
        return new ResponseEntity<>(proxyResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidProductRequestException.class)
    protected ResponseEntity<Object> handleInvalidProductRequestException(InvalidProductRequestException ex) {
        ProxyResponse proxyResponse = responseCreator();
        proxyResponse.setResponseMessage(ex.getMessage());
        return new ResponseEntity<>(proxyResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    protected ResponseEntity<Object> handleBadCredentialsException(BadCredentialsException ex) {
        ProxyResponse proxyResponse = responseCreator();
        proxyResponse.setResponseMessage(ex.getMessage());
        return new ResponseEntity<>(proxyResponse, HttpStatus.BAD_REQUEST);
    }

}
