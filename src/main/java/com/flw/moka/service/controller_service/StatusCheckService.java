package com.flw.moka.service.controller_service;

import java.net.URI;
import java.util.Optional;

import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.flw.moka.entity.constants.Methods;
import com.flw.moka.entity.models.Logs;
import com.flw.moka.entity.models.Refunds;
import com.flw.moka.entity.models.Transaction;
import com.flw.moka.entity.request.ProductRequest;
import com.flw.moka.entity.request.ProviderPayload;
import com.flw.moka.entity.response.PaymentDetail;
import com.flw.moka.entity.response.ProviderResponse;
import com.flw.moka.entity.response.ProviderResponseData;
import com.flw.moka.entity.response.ProxyResponse;
import com.flw.moka.entity.response.StatusCheckResponse;
import com.flw.moka.service.entity_service.LogsService;
import com.flw.moka.service.entity_service.RefundsService;
import com.flw.moka.service.entity_service.TransactionService;
import com.flw.moka.service.helper_service.ProxyResponseService;
import com.flw.moka.utilities.helpers.ProviderApiUtil;
import com.flw.moka.utilities.helpers.TimeUtil;
import com.google.gson.Gson;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class StatusCheckService {

    RefundsService refundsService;
    TransactionService transactionService;
    private Environment environment;
    ProviderApiUtil providerApiUtil;
    ProxyResponseService proxyResponseService;
    LogsService logsService;

    public StatusCheckResponse check(String reference, ProviderPayload providerPayload,
            ProductRequest productRequest) {
        StatusCheckResponse statusCheckResponse = new StatusCheckResponse();

        Optional<Refunds> findRefund = refundsService.getRefund(reference);

        if ((findRefund.isPresent() && !findRefund.get().getResponseCode().equalsIgnoreCase("03"))) {

            StatusCheckResponse providerStatusCheckResponse = getStatusFromProvider(providerPayload,
                    productRequest);

            logStatusCheck(providerStatusCheckResponse);
            return providerStatusCheckResponse;

        } else if ((findRefund.isPresent() && findRefund.get().getResponseCode().equalsIgnoreCase("03"))) {
            Refunds refund = findRefund.get();
            String status = "REFUNDED";
            statusCheckResponse.setRefundDetail(refund);
            statusCheckResponse.setStatusMessage(status);

        } else {
            Optional<Transaction> findTransaction = transactionService.getTransaction(reference);

            if (findTransaction.isPresent() && !findTransaction.get().getResponseCode().equalsIgnoreCase("RR")) {
                Transaction transaction = findTransaction.get();
                String status = transaction.getTransactionStatus();
                statusCheckResponse.setTransactionDetail(transaction);
                statusCheckResponse.setStatusMessage(
                        status.equalsIgnoreCase("Void") ? "VOIDED" : status + "D");
            } else {
                StatusCheckResponse providerStatusCheckResponse = getStatusFromProvider(providerPayload,
                        productRequest);
                logStatusCheck(providerStatusCheckResponse);
                return providerStatusCheckResponse;
            }
        }

        logStatusCheck(statusCheckResponse);
        return statusCheckResponse;
    }

    public StatusCheckResponse getStatusFromProvider(ProviderPayload providerPayload, ProductRequest productRequest) {
        StatusCheckResponse statusCheckResponse = new StatusCheckResponse();
        String statusEndpoint = environment.getProperty("provider.endpoints.status");

        URI endpointURI = URI.create(statusEndpoint);

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
            statusCheckResponse.setStatusMessage("Payment Authorization Pending");
        } else if (preProvisionSuccessful) {
            statusCheckResponse.setStatusMessage("Authorize Successful");
        } else if (preProvisionFailed) {
            statusCheckResponse.setStatusMessage("Authorize Failed");
        } else if (paymentSuccessful) {
            statusCheckResponse.setStatusMessage("Payment Successful");
        } else if (paymentFailed) {
            statusCheckResponse.setStatusMessage("Payment Failed");
        } else if (cancellationSuccessful) {
            statusCheckResponse.setStatusMessage("Void Successful");
        } else if (refundSuccessful) {
            statusCheckResponse.setStatusMessage("Refund Successful");
        } else {
            statusCheckResponse.setStatusMessage("Invalid Payment Status");
        }

        statusCheckResponse.setProviderResponse(paymentDetail);

        return statusCheckResponse;
    }

    private void logStatusCheck(StatusCheckResponse statusCheckResponse) {
        TimeUtil timeUtility = new TimeUtil();
        Logs log = new Logs();
        Gson gson = new Gson();

        String jsonstatusCheckResponse = gson.toJson(statusCheckResponse);

        log.setExternalReference("N/A");
        log.setBody("Status check");
        log.setMethod("status");
        log.setResponse(jsonstatusCheckResponse);
        log.setTransactionReference("N/A");
        log.setTimeIn(timeUtility.getDateTime());

        logsService.saveLogs(log);
    }

}
