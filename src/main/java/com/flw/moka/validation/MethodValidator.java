package com.flw.moka.validation;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.flw.moka.entity.constants.Methods;
import com.flw.moka.entity.models.Refunds;
import com.flw.moka.entity.models.Transaction;
import com.flw.moka.entity.request.ProductRequest;
import com.flw.moka.entity.response.ProxyResponse;
import com.flw.moka.exception.NoMethodNamePassedException;
import com.flw.moka.exception.TransactionMethodAlreadyDoneException;
import com.flw.moka.exception.TransactionNotCapturedException;
import com.flw.moka.exception.TransactionNotFoundException;
import com.flw.moka.service.helper_service.ProxyResponseService;
import com.flw.moka.utilities.helpers.LogsUtil;
import com.flw.moka.utilities.helpers.RefundsUtil;
import com.flw.moka.utilities.helpers.TransactionUtil;

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

    public void preventDuplicateMethodCall(Transaction transaction, String method, ProductRequest productRequest,
            LogsUtil logsUtil, TransactionUtil transactionUtil, Refunds refund, RefundsUtil refundsUtil) {

        Optional<Refunds> findRefund = Optional.ofNullable(refund);

        if (!method.equalsIgnoreCase(Methods.AUTHORIZE)) {

            if (transaction.getTransactionStatus() == null) {
                throw new TransactionNotFoundException("This transaction does not exist");
            }

            boolean transactionAlreadyRefunded = false;

            if (findRefund.isPresent()
                    && findRefund.get().getResponseCode() != null) {
                if (findRefund.get().getResponseCode().equalsIgnoreCase("03"))
                    transactionAlreadyRefunded = true;
            }

            String currentStatus = transaction.getTransactionStatus();

            boolean transactionAlreadyVoided = transaction.getTimeVoided() != null;
            boolean transactionAlreadyCaptured = transaction.getTimeCaptured() != null;

            boolean isCapturingTransaction = method.equalsIgnoreCase(Methods.CAPTURE);
            boolean isVoidingTransaction = method.equalsIgnoreCase(Methods.VOID);
            boolean isRefundingTransaction = method.equalsIgnoreCase(Methods.REFUND);
            boolean methodHasBeenDoneBefore = currentStatus.equalsIgnoreCase(method);

            if (transactionAlreadyVoided && (isVoidingTransaction || isRefundingTransaction)) {
                handleResponse(method, transaction, currentStatus, productRequest, logsUtil, transactionUtil, refund,
                        refundsUtil);
            }

            if (transactionAlreadyCaptured && isCapturingTransaction) {
                handleResponse(method, transaction, currentStatus, productRequest, logsUtil, transactionUtil, refund,
                        refundsUtil);
            }

            if (transactionAlreadyRefunded && (isVoidingTransaction || isRefundingTransaction)) {
                handleResponse(method, transaction, currentStatus, productRequest, logsUtil, transactionUtil, refund,
                        refundsUtil);
            }

            if (methodHasBeenDoneBefore) {
                handleResponse(method, transaction, currentStatus, productRequest, logsUtil, transactionUtil, refund,
                        refundsUtil);
            }
        }
        return;
    }

    private void handleResponse(String method, Transaction transaction, String currentStatus,
            ProductRequest productRequest, LogsUtil logsUtil, TransactionUtil transactionUtil, Refunds refund,
            RefundsUtil refundsUtil) {
        ProxyResponse proxyResponse = new ProxyResponse();

        proxyResponse.setMessage(method.toUpperCase() + " has already been done on this transaction");
        proxyResponse.setProvider("MOKA");

        logsUtil.setLogs(proxyResponse, productRequest, method);

        if (transactionUtil != null) {
            proxyResponse.setCode(transaction.getResponseCode());
            transactionUtil.saveTransactionToDatabase(productRequest, proxyResponse, transaction, method);
        }

        if (refundsUtil != null) {
            proxyResponse.setCode(refund.getResponseCode());
            refundsUtil.saveRefundToDataBase(proxyResponse, refund, transaction);
        }

        throw new TransactionMethodAlreadyDoneException(proxyResponse.getMessage());
    }
}
