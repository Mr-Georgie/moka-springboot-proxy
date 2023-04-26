package com.flw.moka.service.controller_service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.flw.moka.entity.constants.Methods;
import com.flw.moka.entity.models.Transaction;
import com.flw.moka.entity.request.ProductRequest;
import com.flw.moka.entity.request.ProviderPayload;
import com.flw.moka.entity.response.ProxyResponse;
import com.flw.moka.service.entity_service.TransactionService;
import com.flw.moka.utilities.helpers.LogsUtil;
import com.flw.moka.utilities.helpers.MaskCardNumberInProductRequestUtil;
import com.flw.moka.utilities.helpers.ProviderApiUtil;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class AuthorizeService {
    
        TransactionService transactionService;
        ProviderApiUtil providerApiUtil;
        MaskCardNumberInProductRequestUtil maskCardNumberInProductRequestUtility;
        LogsUtil logsUtil;

        public ResponseEntity<ProxyResponse> sendProviderPayload(ProviderPayload providerPayload,
                        ProductRequest productRequest, String method) {

                ProductRequest productRequestWithMaskedCardNumber = maskCardNumberInProductRequestUtility
                                .mask(productRequest);

                ProxyResponse proxyResponse = providerApiUtil
                                .apiCallHandler(method, providerPayload, productRequestWithMaskedCardNumber);


                productRequest.setExternalReference(proxyResponse.getMeta().getExternalReference());

                addEntitiesToDatabase(proxyResponse, productRequestWithMaskedCardNumber);

                return ResponseEntity.status(HttpStatus.OK).body(proxyResponse);
        }

        private void addEntitiesToDatabase(ProxyResponse proxyResponse, ProductRequest productRequest) {
                logsUtil.setLogs(proxyResponse, productRequest, Methods.AUTHORIZE);

                Transaction transaction = new Transaction();
                transactionService.saveTransaction(productRequest, proxyResponse, transaction,
                                Methods.AUTHORIZE);
                return;
        }

}
