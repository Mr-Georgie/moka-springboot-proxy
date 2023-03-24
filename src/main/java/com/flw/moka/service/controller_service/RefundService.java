package com.flw.moka.service.controller_service;

import java.net.URI;
import java.text.ParseException;
import java.util.Optional;

import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.flw.moka.entity.CardParams;
import com.flw.moka.entity.Refunds;
import com.flw.moka.entity.Transaction;
import com.flw.moka.entity.helpers.Methods;
import com.flw.moka.entity.helpers.ProductRequest;
import com.flw.moka.entity.helpers.ProviderPayload;
import com.flw.moka.entity.helpers.ProviderResponse;
import com.flw.moka.entity.helpers.ProviderResponseData;
import com.flw.moka.entity.helpers.ProxyResponse;
import com.flw.moka.service.entity_service.CardParamsService;
import com.flw.moka.service.entity_service.RefundsService;
import com.flw.moka.service.helper_service.ProxyResponseService;
import com.flw.moka.utilities.EntityPreparationUtil;
import com.flw.moka.utilities.ProviderApiUtil;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class RefundService {
	private Environment environment;
	CardParamsService cardParamsService;
	RefundsService refundsService;
	ProxyResponseService proxyResponseService;
	ProviderApiUtil providerApiUtil;

	public ResponseEntity<ProxyResponse> sendProviderPayload(ProviderPayload providerPayload,
			ProductRequest productRequest, Transaction transaction)
			throws ParseException {

		String refundEndpoint = environment.getProperty("provider.endpoints.refund");
		URI endpointURI = URI.create(refundEndpoint);

		ResponseEntity<ProviderResponse> responseEntity = providerApiUtil.makeProviderApiCall(
				endpointURI,
				providerPayload);

		Optional<ProviderResponse> providerResponseBody = providerApiUtil.handleNoProviderResponse(responseEntity);
		Optional<ProviderResponseData> providerResponseData = providerApiUtil
				.unwrapProviderResponse(providerResponseBody);

		ProxyResponse proxyResponse = proxyResponseService.createProxyResponse(providerResponseData,
				providerResponseBody,
				productRequest, Methods.REFUND);

		CardParams cardParams = prepareCardParams(proxyResponse, productRequest);
		cardParamsService.saveCardParams(cardParams);

		Refunds newRefund = createNewRefund(productRequest, proxyResponse);
		refundsService.saveRefund(newRefund);

		return ResponseEntity.ok(proxyResponse);
	}

	private CardParams prepareCardParams(ProxyResponse proxyResponse, ProductRequest productRequest) {
		EntityPreparationUtil entityPreparationUtil = new EntityPreparationUtil(Methods.REFUND);
		return entityPreparationUtil.setCardParams(proxyResponse, productRequest);
	}

	private Refunds createNewRefund(ProductRequest productRequest, ProxyResponse proxyResponse) {
		EntityPreparationUtil entityPreparationUtil = new EntityPreparationUtil(Methods.REFUND);
		return entityPreparationUtil.setRefund(productRequest, proxyResponse);
	}
}
