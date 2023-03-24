package com.flw.moka.utilities;

import java.text.ParseException;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.flw.moka.entity.Transaction;
import com.flw.moka.entity.helpers.ProductRequest;
import com.flw.moka.entity.helpers.ProviderPayload;
import com.flw.moka.entity.helpers.ProxyResponse;
import com.flw.moka.service.controllers.RefundService;
import com.flw.moka.service.controllers.VoidService;
import com.flw.moka.service.entities.TransactionService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class VoidRefundRouterUtil {

    TransactionService transactionService;
    VoidService voidService;
    RefundService refundService;
    MethodValidator methodValidator;

    public ResponseEntity<ProxyResponse> route(ProductRequest productRequest,
            ProviderPayload providerPayload, String method) throws ParseException {
        String productRef = productRequest.getTransactionReference();

        Transaction transaction = transactionService.getTransaction(productRef, method);

        methodValidator.preventVoidOrRefundIfNotCaptured(method, transaction);

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
