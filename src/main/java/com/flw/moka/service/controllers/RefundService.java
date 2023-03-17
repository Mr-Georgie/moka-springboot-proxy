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
public class RefundService {
    private Environment environment;
    CardParamsService cardParamsService;
    TransactionService transactionService;
    ProxyResponseService proxyResponseService;
    ProviderApiUtility providerApiUtility;
    ShouldVoidOrRefundUtility shouldVoidOrRefundUtility;

    public ResponseEntity<ProxyResponse> sendProviderPayload(ProviderPayload providerPayload,
            ProductRequest productRequest)
            throws URISyntaxException, ParseException {

        String authURI = environment.getProperty("provider.endpoints.refund");

        URI url = new URI(authURI);

        String productRef = productRequest.getTransactionReference();

        Transaction transactionNotRefunded = transactionService.getTransaction(productRef, "refund");

        shouldVoidOrRefundUtility.routeTransaction(transactionNotRefunded.getTimeCaptured(), "refund");

        ResponseEntity<ProviderResponse> responseEntity = providerApiUtility.makeApiCall(url, providerPayload);

        Optional<ProviderResponse> optionalBody = providerApiUtility.handleNoResponse(responseEntity);

        Optional<ProviderResponseData> optionalData = providerApiUtility.unwrapResponse(optionalBody);

        ProxyResponse proxyResponse = proxyResponseService.createProxyResponse(optionalData, optionalBody,
                productRequest);

        DbUtility dbUtility = new DbUtility("refund");

        CardParams cardParams = dbUtility.setCardParams(proxyResponse, productRequest);
        cardParamsService.saveCardParams(cardParams);

        Transaction updatedTransaction = updateTransaction(productRequest, transactionNotRefunded, proxyResponse);

        transactionService.saveTransaction(updatedTransaction);

        return new ResponseEntity<>(proxyResponse, HttpStatus.CREATED);
    }

    static Transaction updateTransaction(ProductRequest productRequest, Transaction transaction,
            ProxyResponse proxyResponse) {

        TimeUtility timeUtility = new TimeUtility();

        transaction.setExternalRef(proxyResponse.getExRef());
        transaction.setMessage("successful");
        transaction.setTimeRefunded(timeUtility.getDateTime());
        transaction.setTransactionRef(productRequest.getTransactionReference());
        transaction.setTransactionStatus("refund");

        return transaction;
    }
}
