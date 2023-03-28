package com.flw.moka.service.controller_service;

import java.net.URI;
import java.util.Optional;

import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.flw.moka.entity.constants.Methods;
import com.flw.moka.entity.models.Refunds;
import com.flw.moka.entity.models.Transaction;
import com.flw.moka.entity.request.ProductRequest;
import com.flw.moka.entity.request.ProviderPayload;
import com.flw.moka.entity.response.PaymentDetail;
import com.flw.moka.entity.response.ProviderResponse;
import com.flw.moka.entity.response.ProviderResponseData;
import com.flw.moka.entity.response.ProxyResponse;
import com.flw.moka.entity.response.StatusCheckResponse;
import com.flw.moka.service.entity_service.RefundsService;
import com.flw.moka.service.entity_service.TransactionService;
import com.flw.moka.service.helper_service.ProxyResponseService;
import com.flw.moka.utilities.helpers.ProviderApiUtil;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class StatusCheckService {

    RefundsService refundsService;
    TransactionService transactionService;
    private Environment environment;
    ProviderApiUtil providerApiUtil;
    ProxyResponseService proxyResponseService;

    public StatusCheckResponse check(String reference, ProviderPayload providerPayload,
            ProductRequest productRequest) {
        StatusCheckResponse statusCheckResponse = new StatusCheckResponse();

        Optional<Refunds> findRefund = refundsService.getRefund(reference);

        if ((findRefund.isPresent() && !findRefund.get().getResponseCode().equalsIgnoreCase("03"))) {

            StatusCheckResponse providerStatusCheckResponse = getStatusFromProvider(providerPayload,
                    productRequest);
            return providerStatusCheckResponse;

        } else if ((findRefund.isPresent() && findRefund.get().getResponseCode().equalsIgnoreCase("03"))) {
            Refunds refund = findRefund.get();
            String status = "REFUNDED";
            statusCheckResponse.setRefund(refund);
            statusCheckResponse.setStatus(status);

        } else {
            Optional<Transaction> findTransaction = transactionService.getTransaction(reference);

            if (findTransaction.isPresent()) {
                Transaction transaction = findTransaction.get();
                String status = transaction.getTransactionStatus();
                statusCheckResponse.setTransaction(transaction);
                statusCheckResponse.setStatus(
                        status.equalsIgnoreCase("Void") ? "VOIDED" : status + "D");
            } else {
                StatusCheckResponse providerStatusCheckResponse = getStatusFromProvider(providerPayload,
                        productRequest);
                return providerStatusCheckResponse;
            }
        }

        return statusCheckResponse;
    }

    public StatusCheckResponse getStatusFromProvider(ProviderPayload providerPayload, ProductRequest productRequest) {
        StatusCheckResponse statusCheckResponse = new StatusCheckResponse();
        String authEndpoint = environment.getProperty("provider.endpoints.status");
        URI endpointURI = URI.create(authEndpoint);

        ResponseEntity<ProviderResponse> responseEntity = providerApiUtil.makeProviderApiCall(
                endpointURI, providerPayload);
        Optional<ProviderResponse> providerResponseBody = providerApiUtil.handleNoProviderResponse(responseEntity);
        Optional<ProviderResponseData> providerResponseData = providerApiUtil
                .unwrapProviderResponse(providerResponseBody);

        ProxyResponse proxyResponse = proxyResponseService.createProxyResponse(providerResponseData,
                providerResponseBody,
                productRequest, Methods.STATUS);
        return providerPaymentDetailLogic(proxyResponse, statusCheckResponse);
    }

    private StatusCheckResponse providerPaymentDetailLogic(ProxyResponse proxyResponse,
            StatusCheckResponse statusCheckResponse) {
        PaymentDetail paymentDetail = proxyResponse.getProviderResponse().getData().getPaymentDetail();

        int paymentStatus = paymentDetail.getPaymentStatus();
        int transactionStatus = paymentDetail.getTrxStatus();

        Boolean awaitingPaymentConfirmation = (paymentStatus == 0 && transactionStatus == 0);
        Boolean preProvisionSuccessful = (paymentStatus == 1 && transactionStatus == 1);
        Boolean preProvisionFailed = (paymentStatus == 1 && transactionStatus == 2);
        Boolean paymentSuccessful = (paymentStatus == 2 && transactionStatus == 1);
        Boolean paymentFailed = (paymentStatus == 2 && transactionStatus == 2);
        Boolean cancellationSuccessful = (paymentStatus == 3 && transactionStatus == 1);
        Boolean refundSuccessful = (paymentStatus == 4 && transactionStatus == 1);

        if (awaitingPaymentConfirmation) {
            statusCheckResponse.setStatus("Payment Authorization Pending");
        } else if (preProvisionSuccessful) {
            statusCheckResponse.setStatus("Authorization Successful");
        } else if (preProvisionFailed) {
            statusCheckResponse.setStatus("Authorization Failed");
        } else if (paymentSuccessful) {
            statusCheckResponse.setStatus("Payment Successful");
        } else if (paymentFailed) {
            statusCheckResponse.setStatus("Payment Failed");
        } else if (cancellationSuccessful) {
            statusCheckResponse.setStatus("Void Successful");
        } else if (refundSuccessful) {
            statusCheckResponse.setStatus("Refund Successful");
        } else {
            statusCheckResponse.setStatus("Invalid Payment Status");
        }

        statusCheckResponse.setInfo(paymentDetail);

        return statusCheckResponse;
    }

}
