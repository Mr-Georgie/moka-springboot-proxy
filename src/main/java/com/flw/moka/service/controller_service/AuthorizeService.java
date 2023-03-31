package com.flw.moka.service.controller_service;

import java.net.URI;
import java.util.Optional;

import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.flw.moka.entity.constants.Methods;
import com.flw.moka.entity.models.Transaction;
import com.flw.moka.entity.request.ProductRequest;
import com.flw.moka.entity.request.ProviderPayload;
import com.flw.moka.entity.response.ProviderResponse;
import com.flw.moka.entity.response.ProviderResponseData;
import com.flw.moka.entity.response.ProxyResponse;
import com.flw.moka.service.entity_service.LogsService;
import com.flw.moka.service.entity_service.TransactionService;
import com.flw.moka.service.helper_service.ProxyResponseService;
import com.flw.moka.utilities.helpers.LogsUtil;
import com.flw.moka.utilities.helpers.MaskCardNumberInProductRequestUtil;
import com.flw.moka.utilities.helpers.ProviderApiUtil;
import com.flw.moka.utilities.helpers.TransactionUtil;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class AuthorizeService {

        private Environment environment;
        LogsService logsService;
        TransactionService transactionService;
        ProxyResponseService proxyResponseService;
        ProviderApiUtil providerApiUtil;
        MaskCardNumberInProductRequestUtil maskCardNumberInProductRequestUtility;
        LogsUtil logsUtil;
        TransactionUtil transactionUtil;

        public ResponseEntity<ProxyResponse> sendProviderPayload(ProviderPayload providerPayload,
                        ProductRequest productRequest) {

                String authEndpoint = environment.getProperty("provider.endpoints.authorize");
                URI endpointURI = URI.create(authEndpoint);

                ResponseEntity<ProviderResponse> responseEntity = providerApiUtil.makeProviderApiCall(
                                endpointURI, providerPayload);
                Optional<ProviderResponse> providerResponseBody = providerApiUtil
                                .handleNoProviderResponse(responseEntity);
                Optional<ProviderResponseData> providerResponseData = providerApiUtil
                                .unwrapProviderResponse(providerResponseBody);

                ProductRequest productRequestWithMaskedCardNumber = maskCardNumberInProductRequestUtility
                                .mask(productRequest);

                ProxyResponse proxyResponse = proxyResponseService.createProxyResponse(providerResponseData,
                                providerResponseBody,
                                productRequestWithMaskedCardNumber, Methods.AUTHORIZE);

                productRequest.setExternalReference(proxyResponse.getExternalReference());

                addEntitiesToDatabase(proxyResponse, productRequestWithMaskedCardNumber);

                return ResponseEntity.status(HttpStatus.CREATED).body(proxyResponse);
        }

        private void addEntitiesToDatabase(ProxyResponse proxyResponse, ProductRequest productRequest) {
                logsUtil.setLogs(proxyResponse, productRequest, Methods.AUTHORIZE);

                Transaction transaction = new Transaction();
                transactionUtil.saveTransactionToDatabase(productRequest, proxyResponse, transaction,
                                Methods.AUTHORIZE);
                return;
        }

}
