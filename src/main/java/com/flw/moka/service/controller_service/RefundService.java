package com.flw.moka.service.controller_service;

import java.net.URI;
import java.text.ParseException;
import java.util.Optional;

import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.flw.moka.entity.constants.Methods;
import com.flw.moka.entity.models.Refunds;
import com.flw.moka.entity.models.Transaction;
import com.flw.moka.entity.request.PaymentDealerRequest;
import com.flw.moka.entity.request.ProductRequest;
import com.flw.moka.entity.request.ProviderPayload;
import com.flw.moka.entity.response.ProviderResponse;
import com.flw.moka.entity.response.ProviderResponseData;
import com.flw.moka.entity.response.ProxyResponse;
import com.flw.moka.service.helper_service.ProxyResponseService;
import com.flw.moka.utilities.helpers.LogsUtil;
import com.flw.moka.utilities.helpers.ProviderApiUtil;
import com.flw.moka.utilities.helpers.RefundsUtil;
import com.flw.moka.validation.MethodValidator;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class RefundService {
	private Environment environment;
	RefundsUtil refundsUtil;
	ProxyResponseService proxyResponseService;
	ProviderApiUtil providerApiUtil;
	LogsUtil logsUtil;
	MethodValidator methodValidator;

	public ResponseEntity<ProxyResponse> sendProviderPayload(ProviderPayload providerPayload,
			ProductRequest productRequest, Transaction transaction)
			throws ParseException {

		String method = Methods.REFUND;

		methodValidator
				.preventDuplicateMethodCall(transaction, method, productRequest, logsUtil, null);

		String refundEndpoint = environment.getProperty("provider.endpoints.refund");
		URI endpointURI = URI.create(refundEndpoint);

		Refunds refund = refundsUtil.checkIfRefundExistInDB(productRequest, method);

		PaymentDealerRequest paymentDealerRequest = providerPayload.getPaymentDealerRequest();

		String reference = refund.getTransactionReference() == null ? refund.getRefundReference()
				: refund.getTransactionReference();

		paymentDealerRequest.setOtherTrxCode(reference);

		providerPayload.setPaymentDealerRequest(paymentDealerRequest);

		ResponseEntity<ProviderResponse> responseEntity = providerApiUtil.makeProviderApiCall(
				endpointURI,
				providerPayload);

		Optional<ProviderResponse> providerResponseBody = providerApiUtil.handleNoProviderResponse(responseEntity);
		Optional<ProviderResponseData> providerResponseData = providerApiUtil
				.unwrapProviderResponse(providerResponseBody);

		ProxyResponse proxyResponse = proxyResponseService.createProxyResponse(providerResponseData,
				providerResponseBody,
				productRequest, method);

		addEntitiesToDatabase(proxyResponse, productRequest, transaction, refund);

		return ResponseEntity.status(HttpStatus.OK).body(proxyResponse);
	}

	private void addEntitiesToDatabase(ProxyResponse proxyResponse, ProductRequest productRequest,
			Transaction transaction, Refunds refund) {

		logsUtil.setLogs(proxyResponse, productRequest, Methods.REFUND);

		refundsUtil.saveRefundToDataBase(proxyResponse, refund, transaction);
	}
}
