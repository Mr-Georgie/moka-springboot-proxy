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
import com.flw.moka.entity.helpers.ProductRequest;
import com.flw.moka.entity.helpers.ProviderPayload;
import com.flw.moka.entity.helpers.ProviderResponse;
import com.flw.moka.entity.helpers.ProviderResponseData;
import com.flw.moka.entity.helpers.ProxyResponse;
import com.flw.moka.service.entities.CardParamsService;
import com.flw.moka.service.entities.ProxyResponseService;
import com.flw.moka.service.entities.TransactionService;
import com.flw.moka.utilities.DbUtility;
import com.flw.moka.utilities.MaskCardNumberInProductRequestUtility;
import com.flw.moka.utilities.ProviderApiUtility;
import com.flw.moka.utilities.TimeUtility;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class AuthService {

    private Environment environment;
    CardParamsService cardParamsService;
    TransactionService transactionService;
    ProxyResponseService proxyResponseService;
    ProviderApiUtility providerApiUtility;
    MaskCardNumberInProductRequestUtility maskCardNumberInProductRequestUtility;

    public ResponseEntity<ProxyResponse> sendProviderPayload(ProviderPayload providerPayload,
            ProductRequest productRequest)
            throws URISyntaxException {

        String authURI = environment.getProperty("provider.endpoints.authorize");

        URI url = new URI(authURI);

        ResponseEntity<ProviderResponse> responseEntity = providerApiUtility.makeApiCall(url, providerPayload);

        Optional<ProviderResponse> optionalBody = providerApiUtility.handleNoResponse(responseEntity);

        Optional<ProviderResponseData> optionalData = providerApiUtility.unwrapResponse(optionalBody);

        ProductRequest productRequestWithMaskedCardNumber = maskCardNumberInProductRequestUtility.mask(productRequest);

        ProxyResponse proxyResponse = proxyResponseService.createProxyResponse(optionalData, optionalBody,
                productRequestWithMaskedCardNumber);

        DbUtility dbUtility = new DbUtility("authorize");

        CardParams cardParams = dbUtility.setCardParams(proxyResponse, productRequestWithMaskedCardNumber);
        cardParamsService.saveCardParams(cardParams);

        Transaction transaction = setTransaction(productRequestWithMaskedCardNumber);
        transactionService.saveTransaction(transaction);

        return new ResponseEntity<>(proxyResponse, HttpStatus.CREATED);
    }

    static Transaction setTransaction(ProductRequest productRequest) {

        TimeUtility timeUtility = new TimeUtility();
        Transaction transaction = new Transaction();

        transaction.setAmount(productRequest.getAmount());
        transaction.setCountry(productRequest.getCountry());
        transaction.setCurrency(productRequest.getCurrency());
        transaction.setEmail(productRequest.getEmail());
        transaction.setExternalRef(productRequest.getExternalReference());
        transaction.setMask(productRequest.getCardNo());
        transaction.setMessage("successful");
        transaction.setNarration(productRequest.getNarration());
        transaction.setProvider("MOKA");
        transaction.setTimeAuthorized(timeUtility.getDateTime());
        transaction.setTransactionRef(productRequest.getTransactionReference());
        transaction.setTransactionStatus("Authorized");

        return transaction;
    }

}
