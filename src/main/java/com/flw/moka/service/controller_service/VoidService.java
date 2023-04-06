package com.flw.moka.service.controller_service;

import java.net.URI;
import java.text.ParseException;
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
import com.flw.moka.service.helper_service.ProxyResponseService;
import com.flw.moka.utilities.helpers.LogsUtil;
import com.flw.moka.utilities.helpers.ProviderApiUtil;
import com.flw.moka.utilities.helpers.TransactionUtil;
import com.flw.moka.validation.MethodValidator;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class VoidService {
	private Environment environment;
	TransactionUtil transactionUtil;
	ProxyResponseService proxyResponseService;
	ProviderApiUtil providerApiUtil;
	LogsUtil logsUtil;
	MethodValidator methodValidator;

	public ResponseEntity<ProxyResponse> sendProviderPayload(ProviderPayload providerPayload,
			ProductRequest productRequest,
			Transaction transaction)
			throws ParseException {

		methodValidator
				.preventDuplicateMethodCall(transaction, Methods.VOID, productRequest, logsUtil, transactionUtil);

		String voidEndpoint = environment.getProperty("provider.endpoints.void");
		URI endpointURI = URI.create(voidEndpoint);

		ResponseEntity<ProviderResponse> responseEntity = providerApiUtil.makeProviderApiCall(
				endpointURI,
				providerPayload);
		Optional<ProviderResponse> providerResponseBody = providerApiUtil.handleNoProviderResponse(responseEntity);
		Optional<ProviderResponseData> providerResponseData = providerApiUtil
				.unwrapProviderResponse(providerResponseBody);

		ProxyResponse proxyResponse = proxyResponseService.createProxyResponse(providerResponseData,
				providerResponseBody,
				productRequest, Methods.VOID);

		addEntitiesToDatabase(proxyResponse, productRequest, transaction);

		return ResponseEntity.status(HttpStatus.CREATED).body(proxyResponse);
	}

	// private void HasVoidBeenDoneAlready(Transaction transaction, String method,
	// ProductRequest productRequest) {
	// methodValidator.preventDuplicateMethodCall(transaction, method,
	// productRequest);
	// }

	private void addEntitiesToDatabase(ProxyResponse proxyResponse, ProductRequest productRequest,
			Transaction transaction) {
		logsUtil.setLogs(proxyResponse, productRequest, Methods.VOID);

		transactionUtil.saveTransactionToDatabase(productRequest, proxyResponse, transaction, Methods.VOID);
		return;
	}

}
