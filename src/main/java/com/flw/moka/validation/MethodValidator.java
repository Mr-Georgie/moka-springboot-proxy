package com.flw.moka.validation;

import com.flw.moka.service.entity_service.LogsService;
import org.springframework.stereotype.Component;

import com.flw.moka.entity.constants.Methods;
import com.flw.moka.entity.models.Transaction;
import com.flw.moka.entity.request.ProductRequest;
import com.flw.moka.entity.response.Meta;
import com.flw.moka.entity.response.ProxyResponse;
import com.flw.moka.exception.TransactionMethodAlreadyDoneException;
import com.flw.moka.exception.TransactionNotCapturedException;
import com.flw.moka.exception.TransactionNotFoundException;
import com.flw.moka.service.entity_service.TransactionService;
import com.flw.moka.service.helper_service.ProxyResponseService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class MethodValidator {

    ProxyResponseService proxyResponseService;

    public void preventVoidOrRefundIfNotCaptured(Transaction transaction)
            throws TransactionNotCapturedException {

        if (transaction.getTimeCaptured() == null || transaction.getTimeCaptured().isEmpty()) {
            throw new TransactionNotCapturedException("Please capture transaction before Void/Refund");
        }
    }

    public void preventDuplicateMethodCall(Transaction transaction, String method, ProductRequest productRequest,
                                           LogsService logsService, TransactionService transactionService) {

        if (!method.equalsIgnoreCase(Methods.AUTHORIZE)) {

            if (transaction.getTransactionStatus() == null) {
                throw new TransactionNotFoundException("This transaction does not exist");
            }

            String currentStatus = transaction.getTransactionStatus();

            boolean transactionAlreadyVoided = transaction.getTimeVoided() != null;
            boolean transactionAlreadyCaptured = transaction.getTimeCaptured() != null;
            boolean transactionAlreadyRefunded = transaction.getBalance() == 0L;

            boolean isCapturingTransaction = method.equalsIgnoreCase(Methods.CAPTURE);
            boolean isVoidingTransaction = method.equalsIgnoreCase(Methods.VOID);
            boolean isRefundingTransaction = method.equalsIgnoreCase(Methods.REFUND);
            boolean methodHasBeenDoneBefore = !currentStatus.equalsIgnoreCase(Methods.REFUND) && currentStatus.equalsIgnoreCase(method);

            if (transactionAlreadyVoided && (isVoidingTransaction || isRefundingTransaction)) {
                handleResponse(method, transaction, currentStatus, productRequest, logsService, transactionService);
            }

            if (transactionAlreadyCaptured && isCapturingTransaction) {
                handleResponse(method, transaction, currentStatus, productRequest, logsService, transactionService);
            }

            if (transactionAlreadyRefunded && isRefundingTransaction) {
                handleResponse(method, transaction, currentStatus, productRequest, logsService, transactionService);
            }

            if (methodHasBeenDoneBefore) {
                handleResponse(method, transaction, currentStatus, productRequest, logsService, transactionService);
            }
        }
        return;
    }

    private void handleResponse(String method, Transaction transaction, String currentStatus,
            ProductRequest productRequest, LogsService logsService, TransactionService transactionService) {
        ProxyResponse proxyResponse = new ProxyResponse();
        Meta meta = new Meta();

        meta.setProvider("MOKA");
        proxyResponse.setResponseMessage(method.toUpperCase() + " has already been done on this transaction");
        proxyResponse.setMeta(meta);

        logsService.saveLogs(proxyResponse, productRequest, method);

        proxyResponse.setResponseCode(transaction.getResponseCode());
        transactionService.saveTransaction(productRequest, proxyResponse, transaction, method);

        throw new TransactionMethodAlreadyDoneException(proxyResponse.getResponseMessage());
    }
}
