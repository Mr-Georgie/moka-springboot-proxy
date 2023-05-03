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
import com.flw.moka.utilities.entity.LogsUtil;
import com.flw.moka.utilities.helpers.ProviderApiUtil;
import com.flw.moka.validation.MethodValidator;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class CaptureService {

	TransactionService transactionService;
	ProviderApiUtil providerApiUtil;
	LogsUtil logsUtil;
	MethodValidator methodValidator;

	public ResponseEntity<ProxyResponse> sendProviderPayload(ProviderPayload providerPayload,
        ProductRequest productRequest, String method) {

		Transaction transaction = transactionService.getTransaction(productRequest, method);

		methodValidator.preventDuplicateMethodCall(transaction, method, productRequest, logsUtil, transactionService);

		ProxyResponse proxyResponse = providerApiUtil
                                .apiCallHandler(method, providerPayload, productRequest);

		addEntitiesToDatabase(proxyResponse, productRequest, transaction);

		return ResponseEntity.status(HttpStatus.OK).body(proxyResponse);
	}

	private void addEntitiesToDatabase(ProxyResponse proxyResponse, ProductRequest productRequest,
			Transaction transaction) {
		logsUtil.setLogs(proxyResponse, productRequest, Methods.CAPTURE);

		transactionService.saveTransaction(productRequest, proxyResponse, transaction, Methods.CAPTURE);
		return;
	}

	


}
