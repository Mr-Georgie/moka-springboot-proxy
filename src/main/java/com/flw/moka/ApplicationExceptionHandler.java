package com.flw.moka;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.flw.moka.entity.helpers.ProxyResponse;
import com.flw.moka.exception.InvalidProductRequestException;
import com.flw.moka.exception.NoMethodNamePassedException;
import com.flw.moka.exception.TransactionAlreadyCapturedException;
import com.flw.moka.exception.TransactionAlreadyVoidedException;
import com.flw.moka.exception.TransactionMethodAlreadyDoneException;
import com.flw.moka.exception.TransactionNotCapturedException;
import com.flw.moka.exception.TransactionNotFoundException;
import com.flw.moka.exception.TransactionShouldBeRefundedException;
import com.flw.moka.exception.TransactionShouldBeVoidedException;

@ControllerAdvice
public class ApplicationExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<Object> handleTransactionNotFoundException(TransactionNotFoundException ex) {
        ProxyResponse proxyResponse = new ProxyResponse(ex.getMessage());
        proxyResponse.setCode("RR-404");
        proxyResponse.setTxRef("N/A");
        proxyResponse.setExRef("N/A");
        proxyResponse.setProvider("MOKA");
        return new ResponseEntity<>(proxyResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TransactionAlreadyCapturedException.class)
    protected ResponseEntity<Object> handleTransactionAlreadyCapturedException(TransactionAlreadyCapturedException ex) {
        ProxyResponse proxyResponse = new ProxyResponse(ex.getMessage());
        proxyResponse.setCode("RR-400");
        proxyResponse.setTxRef("N/A");
        proxyResponse.setExRef("N/A");
        proxyResponse.setProvider("MOKA");
        return new ResponseEntity<>(proxyResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TransactionAlreadyVoidedException.class)
    protected ResponseEntity<Object> handleTransactionAlreadyVoidedException(TransactionAlreadyVoidedException ex) {
        ProxyResponse proxyResponse = new ProxyResponse(ex.getMessage());
        proxyResponse.setCode("RR-400");
        proxyResponse.setTxRef("N/A");
        proxyResponse.setExRef("N/A");
        proxyResponse.setProvider("MOKA");
        return new ResponseEntity<>(proxyResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TransactionMethodAlreadyDoneException.class)
    protected ResponseEntity<Object> handleTransactionMethodAlreadyDoneException(
            TransactionMethodAlreadyDoneException ex) {
        ProxyResponse proxyResponse = new ProxyResponse(ex.getMessage());
        proxyResponse.setCode("RR-400");
        proxyResponse.setTxRef("N/A");
        proxyResponse.setExRef("N/A");
        proxyResponse.setProvider("MOKA");
        return new ResponseEntity<>(proxyResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TransactionShouldBeVoidedException.class)
    protected ResponseEntity<Object> handleTransactionShouldBeVoidedException(TransactionShouldBeVoidedException ex) {
        ProxyResponse proxyResponse = new ProxyResponse(ex.getMessage());
        proxyResponse.setCode("RR-400");
        proxyResponse.setTxRef("N/A");
        proxyResponse.setExRef("N/A");
        proxyResponse.setProvider("MOKA");
        return new ResponseEntity<>(proxyResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TransactionShouldBeRefundedException.class)
    protected ResponseEntity<Object> handleTransactionShouldBeRefundedException(
            TransactionShouldBeRefundedException ex) {
        ProxyResponse proxyResponse = new ProxyResponse(ex.getMessage());
        proxyResponse.setCode("RR-400");
        proxyResponse.setTxRef("N/A");
        proxyResponse.setExRef("N/A");
        proxyResponse.setProvider("MOKA");
        return new ResponseEntity<>(proxyResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TransactionNotCapturedException.class)
    protected ResponseEntity<Object> handleTransactionNotCapturedException(TransactionNotCapturedException ex) {
        ProxyResponse proxyResponse = new ProxyResponse(ex.getMessage());
        proxyResponse.setCode("RR-400");
        proxyResponse.setTxRef("N/A");
        proxyResponse.setExRef("N/A");
        proxyResponse.setProvider("MOKA");
        return new ResponseEntity<>(proxyResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoMethodNamePassedException.class)
    protected ResponseEntity<Object> handleNoMethodPassedException(NoMethodNamePassedException ex) {
        ProxyResponse proxyResponse = new ProxyResponse(ex.getMessage());
        proxyResponse.setCode("RR-400");
        proxyResponse.setTxRef("N/A");
        proxyResponse.setExRef("N/A");
        proxyResponse.setProvider("MOKA");
        return new ResponseEntity<>(proxyResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidProductRequestException.class)
    protected ResponseEntity<Object> handleInvalidProductRequestException(InvalidProductRequestException ex) {
        ProxyResponse proxyResponse = new ProxyResponse(ex.getMessage());
        proxyResponse.setCode("RR-400");
        proxyResponse.setTxRef("N/A");
        proxyResponse.setExRef("N/A");
        proxyResponse.setProvider("MOKA");
        return new ResponseEntity<>(proxyResponse, HttpStatus.BAD_REQUEST);
    }

}
