package com.flw.moka.service.controller_service;

import java.net.URI;
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
import com.flw.moka.service.entity_service.CardParamsService;
import com.flw.moka.service.entity_service.TransactionService;
import com.flw.moka.service.helper_service.ProxyResponseService;
import com.flw.moka.utilities.EntityPreparationUtil;
import com.flw.moka.utilities.MaskCardNumberInProductRequestUtil;
import com.flw.moka.utilities.ProviderApiUtil;
import com.flw.moka.utilities.TimeUtil;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class AuthService {

    private Environment environment;
    CardParamsService cardParamsService;
    TransactionService transactionService;
    ProxyResponseService proxyResponseService;
    ProviderApiUtil providerApiUtil;
    MaskCardNumberInProductRequestUtil maskCardNumberInProductRequestUtility;

    public ResponseEntity<ProxyResponse> sendProviderPayload(ProviderPayload providerPayload,
            ProductRequest productRequest) {

        String authEndpoint = environment.getProperty("provider.endpoints.authorize");
        URI endpointURI = URI.create(authEndpoint);

        ResponseEntity<ProviderResponse> responseEntity = providerApiUtil.makeProviderApiCall(
                endpointURI, providerPayload);
        Optional<ProviderResponse> providerResponseBody = providerApiUtil.handleNoProviderResponse(responseEntity);
        Optional<ProviderResponseData> providerResponseData = providerApiUtil
                .unwrapProviderResponse(providerResponseBody);

        ProductRequest productRequestWithMaskedCardNumber = maskCardNumberInProductRequestUtility.mask(productRequest);

        ProxyResponse proxyResponse = proxyResponseService.createProxyResponse(providerResponseData,
                providerResponseBody,
                productRequestWithMaskedCardNumber, Methods.AUTHORIZE);

        productRequest.setExternalReference(proxyResponse.getExRef());

        CardParams cardParams = prepareCardParams(proxyResponse, productRequestWithMaskedCardNumber);
        cardParamsService.saveCardParams(cardParams);

        Transaction transaction = setTransaction(productRequestWithMaskedCardNumber);
        transactionService.saveTransaction(transaction);

        return ResponseEntity.status(HttpStatus.CREATED).body(proxyResponse);
    }

    private CardParams prepareCardParams(ProxyResponse proxyResponse, ProductRequest productRequest) {
        EntityPreparationUtil entityPreparationUtil = new EntityPreparationUtil(Methods.AUTHORIZE);
        return entityPreparationUtil.setCardParams(proxyResponse, productRequest);
    }

    private Transaction setTransaction(ProductRequest productRequest) {

        TimeUtil timeUtility = new TimeUtil();
        Transaction transaction = new Transaction();

        transaction.setAmount(productRequest.getAmount());
        transaction.setCountry(productRequest.getCountry());
        transaction.setCurrency(productRequest.getCurrency());
        transaction.setExternalRef(productRequest.getExternalReference());
        transaction.setMask(productRequest.getCardNo());
        transaction.setTimeAuthorized(timeUtility.getDateTime());
        transaction.setTransactionRef(productRequest.getTransactionReference());
        transaction.setTransactionStatus(Methods.AUTHORIZE.toUpperCase());
        transaction.setResponseMessage("Pending Capture");
        transaction.setResponseCode("02");
        transaction.setNarration("CARD Transaction");
        transaction.setProvider("MOKA");

        return transaction;
    }

}
