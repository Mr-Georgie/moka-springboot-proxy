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
import com.flw.moka.service.entity_service.TransactionService;
import com.flw.moka.service.helper_service.ProxyResponseService;
import com.flw.moka.utilities.helpers.LogsUtil;
import com.flw.moka.utilities.helpers.ProviderApiUtil;
import com.flw.moka.validation.MethodValidator;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class CaptureService {

	private Environment environment;
	TransactionService transactionService;
	ProxyResponseService proxyResponseService;
	ProviderApiUtil providerApiUtil;
	LogsUtil logsUtil;
	MethodValidator methodValidator;

	public ResponseEntity<ProxyResponse> sendProviderPayload(ProviderPayload providerPayload,
        ProductRequest productRequest, String method) {

		String endpoint = environment.getProperty("provider.endpoints." + method);
		URI endpointURI = URI.create(endpoint);

		Transaction transaction = transactionService.getTransaction(productRequest, method);

		methodValidator.preventDuplicateMethodCall(transaction, method, productRequest, logsUtil, transactionService);

		ResponseEntity<ProviderResponse> responseEntity = providerApiUtil.makeProviderApiCall(endpointURI, providerPayload);
		Optional<ProviderResponse> providerResponse = providerApiUtil.handleNoProviderResponse(responseEntity);
		Optional<ProviderResponseData> providerResponseData = providerApiUtil.unwrapProviderResponse(providerResponse);

		ProxyResponse proxyResponse = proxyResponseService.createProxyResponse(providerResponseData, providerResponse,
				productRequest, method);

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
