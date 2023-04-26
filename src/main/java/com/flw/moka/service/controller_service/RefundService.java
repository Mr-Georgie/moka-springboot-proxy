package com.flw.moka.service.controller_service;

import java.text.ParseException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.flw.moka.entity.constants.Methods;
import com.flw.moka.entity.models.Refunds;
import com.flw.moka.entity.models.Transaction;
import com.flw.moka.entity.request.ProductRequest;
import com.flw.moka.entity.request.ProviderPayload;
import com.flw.moka.entity.response.ProxyResponse;
import com.flw.moka.utilities.helpers.LogsUtil;
import com.flw.moka.utilities.helpers.ProviderApiUtil;
import com.flw.moka.utilities.helpers.RefundsUtil;
import com.flw.moka.validation.MethodValidator;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class RefundService {

	RefundsUtil refundsUtil;
	ProviderApiUtil providerApiUtil;
	LogsUtil logsUtil;
	MethodValidator methodValidator;

	public ResponseEntity<ProxyResponse> sendProviderPayload(ProviderPayload providerPayload,
			ProductRequest productRequest, Transaction transaction, String method)
			throws ParseException {

		Refunds refund = refundsUtil.checkIfRefundExistInDB(productRequest);

		methodValidator
				.preventDuplicateMethodCall(transaction, method, productRequest, logsUtil, null);

		ProxyResponse proxyResponse = providerApiUtil
                                .apiCallHandler(method, providerPayload, productRequest);
				

		addEntitiesToDatabase(proxyResponse, productRequest, transaction, refund);

		return ResponseEntity.status(HttpStatus.OK).body(proxyResponse);
	}

	private void addEntitiesToDatabase(ProxyResponse proxyResponse, ProductRequest productRequest,
			Transaction transaction, Refunds refund) {

		logsUtil.setLogs(proxyResponse, productRequest, Methods.REFUND);

		refundsUtil.saveRefundToDataBase(proxyResponse, refund, transaction);
	}
}
