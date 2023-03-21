package com.flw.moka.service.controllers;

import java.net.URI;
import java.text.ParseException;
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
import com.flw.moka.service.entities.CardParamsService;
import com.flw.moka.service.entities.ProxyResponseService;
import com.flw.moka.service.entities.TransactionService;
import com.flw.moka.utilities.EntityPreparationUtil;
import com.flw.moka.utilities.ProviderApiUtil;
import com.flw.moka.utilities.ShouldVoidOrRefundUtil;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class RefundService {
	private Environment environment;
	CardParamsService cardParamsService;
	TransactionService transactionService;
	ProxyResponseService proxyResponseService;
	ProviderApiUtil providerApiUtil;
	ShouldVoidOrRefundUtil shouldVoidOrRefundUtility;

	public ResponseEntity<ProxyResponse> sendProviderPayload(ProviderPayload providerPayload,
			ProductRequest productRequest)
			throws ParseException {

		String refundEndpoint = environment.getProperty("provider.endpoints.refund");
		URI endpointURI = URI.create(refundEndpoint);

		String productRef = productRequest.getTransactionReference();

		Transaction transactionNotRefunded = transactionService.getTransaction(productRef, Methods.REFUND);
		shouldVoidOrRefundUtility.routeTransaction(transactionNotRefunded.getTimeCaptured(), Methods.REFUND);

		ResponseEntity<ProviderResponse> responseEntity = providerApiUtil.makeProviderApiCall(
				endpointURI,
				providerPayload);

		Optional<ProviderResponse> providerResponseBody = providerApiUtil.handleNoProviderResponse(responseEntity);
		Optional<ProviderResponseData> providerResponseData = providerApiUtil
				.unwrapProviderResponse(providerResponseBody);

		ProxyResponse proxyResponse = proxyResponseService.createProxyResponse(providerResponseData,
				providerResponseBody,
				productRequest);

		CardParams cardParams = prepareCardParams(proxyResponse, productRequest);
		cardParamsService.saveCardParams(cardParams);

		Transaction updatedTransaction = updateTransactionStatus(productRequest, transactionNotRefunded, proxyResponse);
		transactionService.saveTransaction(updatedTransaction);

		return ResponseEntity.ok(proxyResponse);
	}

	private CardParams prepareCardParams(ProxyResponse proxyResponse, ProductRequest productRequest) {
		EntityPreparationUtil entityPreparationUtil = new EntityPreparationUtil(Methods.REFUND);
		return entityPreparationUtil.setCardParams(proxyResponse, productRequest);
	}

	private Transaction updateTransactionStatus(ProductRequest productRequest, Transaction transaction,
			ProxyResponse proxyResponse) {
		EntityPreparationUtil entityPreparationUtil = new EntityPreparationUtil(Methods.REFUND);
		return entityPreparationUtil.updateTransactionStatus(productRequest, transaction, proxyResponse);
	}
}
