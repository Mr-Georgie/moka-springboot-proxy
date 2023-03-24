package com.flw.moka.validation;

import org.springframework.stereotype.Component;

import com.flw.moka.entity.Transaction;
import com.flw.moka.entity.helpers.Methods;
import com.flw.moka.entity.helpers.ProxyResponse;
import com.flw.moka.exception.NoMethodNamePassedException;
import com.flw.moka.exception.TransactionNotCapturedException;
import com.flw.moka.service.helper_service.ProxyResponseService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class MethodValidator {

    ProxyResponseService proxyResponseService;

    public void preventVoidOrRefundIfNotCaptured(String method, Transaction transaction)
            throws TransactionNotCapturedException {

        if (transaction.getTimeCaptured() == null || transaction.getTimeCaptured().isEmpty()) {
            throw new TransactionNotCapturedException("Can't " + method + " a transaction that is not captured");
        }
        if (method == null || method.isEmpty()) {
            throw new NoMethodNamePassedException(
                    "Please provide a method in your void/refund service to use this utility");
        }
    }

    public Transaction preventDuplicateMethodCall(Transaction transaction, String transactionRef, String method) {
        String currentStatus = transaction.getTransactionStatus();

        boolean transactionAlreadyVoided = transaction.getTimeVoided() != null;
        boolean transactionAlreadyCaptured = transaction.getTimeCaptured() != null;
        boolean transactionAlreadyRefunded = transaction.getTimeRefunded() != null;

        boolean isCapturingTransaction = method.equalsIgnoreCase(Methods.CAPTURE);
        boolean isVoidingTransaction = method.equalsIgnoreCase(Methods.VOID);
        boolean isRefundingTransaction = method.equalsIgnoreCase(Methods.REFUND);
        boolean methodHasBeenDoneBefore = currentStatus.equalsIgnoreCase(method);

        if (transactionAlreadyVoided && (isVoidingTransaction || isRefundingTransaction)) {
            handleResponse(method, transactionRef, currentStatus);
        }

        if (transactionAlreadyCaptured && isCapturingTransaction) {
            handleResponse(method, transactionRef, currentStatus);
        }

        if (transactionAlreadyRefunded && (isVoidingTransaction || isRefundingTransaction)) {
            handleResponse(method, transactionRef, currentStatus);
        }

        if (methodHasBeenDoneBefore) {
            handleResponse(method, transactionRef, currentStatus);
        }
        return transaction;
    }

    private Transaction handleResponse(String method, String transactionRef, String currentStatus) {
        ProxyResponse proxyResponse = new ProxyResponse();

        proxyResponse.setMessage("This transaction status is: " + currentStatus.toUpperCase());
        proxyResponse.setCode("RR-400");
        proxyResponse.setProvider("MOKA");

        proxyResponseService.saveFailedResponseToDB(proxyResponse, transactionRef, method);

        return proxyResponseService.sendMethodAlreadyDoneResponse(currentStatus, transactionRef, Transaction.class);
    }
}
