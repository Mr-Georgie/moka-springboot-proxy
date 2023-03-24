package com.flw.moka.service.controller_service;

import java.net.URI;
import java.util.Optional;

import org.springframework.core.env.Environment;
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
import com.flw.moka.utilities.ProviderApiUtil;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class CaptureService {

	private Environment environment;
	CardParamsService cardParamsService;
	TransactionService transactionService;
	ProxyResponseService proxyResponseService;
	ProviderApiUtil providerApiUtil;

	public ResponseEntity<ProxyResponse> sendProviderPayload(ProviderPayload providerPayload,
			ProductRequest productRequest) {

		String captureEndpoint = environment.getProperty("provider.endpoints.capture");
		URI endpointURI = URI.create(captureEndpoint);

		String transactionReference = productRequest.getTransactionReference();

		Transaction transaction = transactionService.getTransaction(transactionReference, Methods.CAPTURE);

		ResponseEntity<ProviderResponse> providerResponseEntity = providerApiUtil.makeProviderApiCall(endpointURI,
				providerPayload);
		Optional<ProviderResponse> providerResponse = providerApiUtil.handleNoProviderResponse(providerResponseEntity);
		Optional<ProviderResponseData> providerResponseData = providerApiUtil.unwrapProviderResponse(providerResponse);

		ProxyResponse proxyResponse = proxyResponseService.createProxyResponse(providerResponseData, providerResponse,
				productRequest, Methods.CAPTURE);
		CardParams cardParams = prepareCardParams(proxyResponse, productRequest);
		cardParamsService.saveCardParams(cardParams);

		Transaction updatedTransaction = updateTransactionStatus(productRequest, transaction, proxyResponse);
		transactionService.saveTransaction(updatedTransaction);

		return ResponseEntity.ok(proxyResponse);
	}

	private CardParams prepareCardParams(ProxyResponse proxyResponse, ProductRequest productRequest) {
		EntityPreparationUtil entityPreparationUtil = new EntityPreparationUtil(Methods.CAPTURE);
		return entityPreparationUtil.setCardParams(proxyResponse, productRequest);
	}

	private Transaction updateTransactionStatus(ProductRequest productRequest, Transaction transaction,
			ProxyResponse proxyResponse) {
		EntityPreparationUtil entityPreparationUtil = new EntityPreparationUtil(Methods.CAPTURE);
		return entityPreparationUtil.updateTransactionStatus(productRequest, transaction, proxyResponse);
	}

}
