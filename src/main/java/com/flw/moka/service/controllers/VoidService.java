package com.flw.moka.service.controllers;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Optional;

import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.flw.moka.entity.CardParams;
import com.flw.moka.entity.ProductRequest;
import com.flw.moka.entity.ProviderPayload;
import com.flw.moka.entity.ProviderResponse;
import com.flw.moka.entity.ProviderResponseData;
import com.flw.moka.entity.ProxyResponse;
import com.flw.moka.entity.Transaction;
import com.flw.moka.service.entities.CardParamsService;
import com.flw.moka.service.entities.ProxyResponseService;
import com.flw.moka.service.entities.TransactionService;
import com.flw.moka.utilities.DbUtility;
import com.flw.moka.utilities.ProviderApiUtility;
import com.flw.moka.utilities.ShouldVoidOrRefundUtility;
import com.flw.moka.utilities.TimeUtility;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class VoidService {
    private Environment environment;
    CardParamsService cardParamsService;
    TransactionService transactionService;
    ProxyResponseService proxyResponseService;
    ProviderApiUtility providerApiUtility;
    ShouldVoidOrRefundUtility shouldVoidOrRefundUtility;

    public ResponseEntity<ProxyResponse> sendProviderPayload(ProviderPayload providerPayload,
            ProductRequest productRequest)
            throws URISyntaxException, ParseException {

        String authURI = environment.getProperty("provider.endpoints.void");

        URI url = new URI(authURI);

        String productRef = productRequest.getTransactionReference();

        Transaction transactionNotVoided = transactionService.getTransaction(productRef, "void");

        shouldVoidOrRefundUtility.routeTransaction(transactionNotVoided.getTimeCaptured(), "void");

        ResponseEntity<ProviderResponse> responseEntity = providerApiUtility.makeApiCall(url, providerPayload);

        Optional<ProviderResponse> optionalBody = providerApiUtility.handleNoResponse(responseEntity);

        Optional<ProviderResponseData> optionalData = providerApiUtility.unwrapResponse(optionalBody);

        ProxyResponse proxyResponse = proxyResponseService.createProxyResponse(optionalData, optionalBody,
                productRequest);

        DbUtility dbUtility = new DbUtility("void");

        CardParams cardParams = dbUtility.setCardParams(proxyResponse, productRequest);
        cardParamsService.saveCardParams(cardParams);

        Transaction updatedTransaction = updateTransaction(productRequest, transactionNotVoided, proxyResponse);

        transactionService.saveTransaction(updatedTransaction);

        return new ResponseEntity<>(proxyResponse, HttpStatus.CREATED);
    }

    static Transaction updateTransaction(ProductRequest productRequest, Transaction transaction,
            ProxyResponse proxyResponse) {

        TimeUtility timeUtility = new TimeUtility();

        transaction.setExternalRef(proxyResponse.getExRef());
        transaction.setMessage("successful");
        transaction.setTimeVoided(timeUtility.getDateTime());
        transaction.setTransactionRef(productRequest.getTransactionReference());
        transaction.setTransactionStatus("void");

        return transaction;
    }
}
