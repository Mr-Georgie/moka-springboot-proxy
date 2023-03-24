package com.flw.moka.controller.custom_router;

import java.text.ParseException;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.flw.moka.entity.Transaction;
import com.flw.moka.entity.helpers.ProductRequest;
import com.flw.moka.entity.helpers.ProviderPayload;
import com.flw.moka.entity.helpers.ProxyResponse;
import com.flw.moka.service.controller_service.RefundService;
import com.flw.moka.service.controller_service.VoidService;
import com.flw.moka.service.entity_service.TransactionService;
import com.flw.moka.utilities.TimeUtil;
import com.flw.moka.validation.MethodValidator;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class VoidRefundRouter {

    TransactionService transactionService;
    VoidService voidService;
    RefundService refundService;
    MethodValidator methodValidator;

    public ResponseEntity<ProxyResponse> route(ProductRequest productRequest,
            ProviderPayload providerPayload, String method) throws ParseException {
        String productRef = productRequest.getTransactionReference();

        Transaction transaction = transactionService.getTransaction(productRef, method);

        methodValidator.preventVoidOrRefundIfNotCaptured(method, transaction);
        // methodValidator.preventDuplicateMethodCall(transaction, productRef, method);

        String transactionTimeCaptured = transaction.getTimeCaptured();

        TimeUtil timeUtility = new TimeUtil();
        Boolean isTransactionUpTo24Hours = timeUtility.isTransactionUpTo24Hours(transactionTimeCaptured);

        if (isTransactionUpTo24Hours) {

            // This transaction should be refunded
            ResponseEntity<ProxyResponse> responseEntity = refundService.sendProviderPayload(
                    providerPayload,
                    productRequest, transaction);

            return responseEntity;
        } else {
            // This transaction should be voided

            ResponseEntity<ProxyResponse> responseEntity = voidService.sendProviderPayload(
                    providerPayload,
                    productRequest, transaction);

            return responseEntity;
        }
    }
}
