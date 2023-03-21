package com.flw.moka.service.controllers;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.flw.moka.entity.CardParams;
import com.flw.moka.entity.Transaction;
import com.flw.moka.entity.helpers.Methods;
import com.flw.moka.entity.helpers.ProductRequest;
import com.flw.moka.entity.helpers.ProviderPayload;
import com.flw.moka.entity.helpers.ProviderResponse;
import com.flw.moka.entity.helpers.ProviderResponseData;
import com.flw.moka.entity.helpers.ProxyResponse;
import com.flw.moka.service.entities.CardParamsService;
import com.flw.moka.service.entities.ProxyResponseService;
import com.flw.moka.service.entities.TransactionService;
import com.flw.moka.utilities.DbUtility;
import com.flw.moka.utilities.ProviderApiUtility;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class CaptureService {

    private Environment environment;
    CardParamsService cardParamsService;
    TransactionService transactionService;
    ProxyResponseService proxyResponseService;
    ProviderApiUtility providerApiUtility;

    public ResponseEntity<ProxyResponse> sendProviderPayload(ProviderPayload providerPayload,
            ProductRequest productRequest)
            throws URISyntaxException {

        String authURI = environment.getProperty("provider.endpoints.capture");

        URI url = new URI(authURI);

        String productRef = productRequest.getTransactionReference();

        Transaction getTransactionIfNotCaptured = transactionService.getTransaction(productRef, "capture");

        ResponseEntity<ProviderResponse> responseEntity = providerApiUtility.makeApiCall(url, providerPayload);

        Optional<ProviderResponse> optionalBody = providerApiUtility.handleNoResponse(responseEntity);

        Optional<ProviderResponseData> optionalData = providerApiUtility.unwrapResponse(optionalBody);

        ProxyResponse proxyResponse = proxyResponseService.createProxyResponse(optionalData, optionalBody,
                productRequest);

        DbUtility dbUtility = new DbUtility(Methods.CAPTURE);

        CardParams cardParams = dbUtility.setCardParams(proxyResponse, productRequest);
        cardParamsService.saveCardParams(cardParams);

        Transaction updatedTransaction = updateTransaction(productRequest, getTransactionIfNotCaptured, proxyResponse);

        transactionService.saveTransaction(updatedTransaction);

        return new ResponseEntity<>(proxyResponse, HttpStatus.CREATED);
    }

    static Transaction updateTransaction(ProductRequest productRequest, Transaction transaction,
            ProxyResponse proxyResponse) {

        // TimeUtility timeUtility = new TimeUtility();

        transaction.setAmount(productRequest.getAmount());
        transaction.setExternalRef(proxyResponse.getExRef());
        transaction.setMessage("successful");
        transaction.setTransactionRef(productRequest.getTransactionReference());
        transaction.setTransactionStatus(Methods.CAPTURE.toUpperCase());
        transaction.setTimeCaptured("2023-03-20 22:00:00");
        // transaction.setTimeCaptured(timeUtility.getDateTime());

        return transaction;
    }

}
