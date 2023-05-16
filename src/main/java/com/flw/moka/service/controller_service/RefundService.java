package com.flw.moka.service.controller_service;

import java.text.ParseException;

import com.flw.moka.service.entity_service.LogsService;
import com.flw.moka.service.entity_service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.flw.moka.entity.constants.Methods;
import com.flw.moka.entity.models.Transaction;
import com.flw.moka.entity.request.ProductRequest;
import com.flw.moka.entity.request.ProviderPayload;
import com.flw.moka.entity.response.ProxyResponse;
import com.flw.moka.utilities.helpers.ProviderApiUtil;
import com.flw.moka.validation.MethodValidator;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class RefundService {

    ProviderApiUtil providerApiUtil;
    LogsService logsService;
    MethodValidator methodValidator;
    TransactionService transactionService;

    public ResponseEntity<ProxyResponse> sendProviderPayload(ProviderPayload providerPayload,
                                                             ProductRequest productRequest, Transaction transaction, String method) {

        methodValidator.preventDuplicateMethodCall(transaction, method, productRequest, logsService, transactionService);

        ProxyResponse proxyResponse = providerApiUtil
                .apiCallHandler(method, providerPayload, productRequest);

        addEntitiesToDatabase(proxyResponse, productRequest, transaction);

        return ResponseEntity.status(HttpStatus.OK).body(proxyResponse);
    }

    private void addEntitiesToDatabase(ProxyResponse proxyResponse, ProductRequest productRequest,
                                       Transaction transaction) {

        logsService.saveLogs(proxyResponse, productRequest, Methods.REFUND);
        transactionService.saveTransaction(productRequest, proxyResponse, transaction, Methods.REFUND);
    }
}
