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
import com.flw.moka.entity.response.StatusCheck;
import com.flw.moka.entity.response.StatusCheckResponse;
import com.flw.moka.entity.response.StatusMeta;
import com.flw.moka.service.entity_service.LogsService;
import com.flw.moka.service.entity_service.RefundsEntityService;
import com.flw.moka.service.entity_service.TransactionService;
import com.flw.moka.service.helper_service.ProxyResponseService;
import com.flw.moka.utilities.helpers.ProviderApiUtil;
import com.flw.moka.utilities.helpers.TimeUtil;
import com.google.gson.Gson;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class StatusCheckService {

    RefundsEntityService refundsService;
    TransactionService transactionService;
    private Environment environment;
    ProviderApiUtil providerApiUtil;
    ProxyResponseService proxyResponseService;
    LogsService logsService;

    public StatusCheckResponse check(String reference, ProviderPayload providerPayload,
            ProductRequest productRequest) {
        StatusCheck statusCheck = new StatusCheck();

        Optional<Refunds> findRefund = refundsService.getRefundByRefundReference(reference);

        if ((findRefund.isPresent() && !findRefund.get().getResponseCode().equalsIgnoreCase("03"))) {

            StatusCheck providerStatusCheck = getStatusFromProvider(providerPayload,
                    productRequest);

            logStatusCheck(providerStatusCheck);
            return setStatusCheckResponseMeta(providerStatusCheck, "is-provider-detail");

            // return providerStatusCheck;

        } else if ((findRefund.isPresent() && findRefund.get().getResponseCode().equalsIgnoreCase("03"))) {
            Refunds refund = findRefund.get();
            String status = "REFUNDED";
            statusCheck.setRefundDetail(refund);
            statusCheck.setStatusMessage(status);

            logStatusCheck(statusCheck);
            return setStatusCheckResponseMeta(statusCheck, "is-refund-detail");

        } else {
            Optional<Transaction> findTransaction = transactionService.getTransaction(reference);

            if (findTransaction.isPresent() && !findTransaction.get().getResponseCode().equalsIgnoreCase("RR")) {
                Transaction transaction = findTransaction.get();
                String status = transaction.getTransactionStatus();
                statusCheck.setTransactionDetail(transaction);
                statusCheck.setStatusMessage(
                        status.equalsIgnoreCase("Void") ? "VOIDED" : status + "D");

                logStatusCheck(statusCheck);
                return setStatusCheckResponseMeta(statusCheck, "is-transaction-detail");
            } else {
                StatusCheck providerStatusCheck = getStatusFromProvider(providerPayload,
                        productRequest);
                logStatusCheck(providerStatusCheck);

                return setStatusCheckResponseMeta(providerStatusCheck, "is-provider-detail");
                // return providerStatusCheck;
            }
        }

        // logStatusCheck(statusCheck);
        // return statusCheck;
    }

    public StatusCheck getStatusFromProvider(ProviderPayload providerPayload, ProductRequest productRequest) {
        StatusCheck statusCheck = new StatusCheck();
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
        return providerPaymentDetailLogic(proxyResponse, statusCheck);
    }

    private StatusCheck providerPaymentDetailLogic(ProxyResponse proxyResponse,
            StatusCheck statusCheck) {
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
            statusCheck.setStatusMessage("Payment Authorization Pending");
        } else if (preProvisionSuccessful) {
            statusCheck.setStatusMessage("Authorize Successful");
        } else if (preProvisionFailed) {
            statusCheck.setStatusMessage("Authorize Failed");
        } else if (paymentSuccessful) {
            statusCheck.setStatusMessage("Payment Successful");
        } else if (paymentFailed) {
            statusCheck.setStatusMessage("Payment Failed");
        } else if (cancellationSuccessful) {
            statusCheck.setStatusMessage("Void Successful");
        } else if (refundSuccessful) {
            statusCheck.setStatusMessage("Refund Successful");
        } else {
            statusCheck.setStatusMessage("Invalid Payment Status");
        }

        statusCheck.setProviderResponse(paymentDetail);

        return statusCheck;
    }

    private void logStatusCheck(StatusCheck statusCheck) {
        TimeUtil timeUtility = new TimeUtil();
        Logs log = new Logs();
        Gson gson = new Gson();

        String jsonstatusCheck = gson.toJson(statusCheck);

        log.setExternalReference("N/A");
        log.setBody("Status check");
        log.setMethod("status");
        log.setResponse(jsonstatusCheck);
        log.setTransactionReference("N/A");
        log.setTimeIn(timeUtility.getDateTime());

        logsService.saveLogs(log);
    }

    private StatusCheckResponse setStatusCheckResponseMeta(StatusCheck statusCheck, String detailInfo) {
        StatusCheckResponse statusCheckResponse = new StatusCheckResponse();
        StatusMeta statusMeta = new StatusMeta();

        if (detailInfo.equalsIgnoreCase("is-provider-detail")) {
            statusMeta.setAmount(Long.valueOf(statusCheck.getProviderResponse().getAmount()));
            statusMeta.setCardholderName(statusCheck.getProviderResponse().getCardHolderFullName());
            statusMeta.setCurrency(statusCheck.getProviderResponse().getCurrencyCode());
            statusMeta.setMask(null);
            statusMeta.setPaymentDate(statusCheck.getProviderResponse().getPaymentDate());
            statusMeta.setRefundedAmount(Long.valueOf(statusCheck.getProviderResponse().getRefAmount()));
            statusMeta.setTransactionReference(statusCheck.getProviderResponse().getOtherTrxCode());

            statusCheckResponse.setStatusMessage(statusCheck.getStatusMessage());
            statusCheckResponse.setStatusMeta(statusMeta);

        } else if (detailInfo.equalsIgnoreCase("is-refund-detail")) {
            statusMeta.setAmount(statusCheck.getRefundDetail().getRefundedAmount());
            statusMeta.setCardholderName("");
            statusMeta.setCurrency(statusCheck.getRefundDetail().getCurrency());
            statusMeta.setMask(statusCheck.getRefundDetail().getMask());
            statusMeta.setPaymentDate(statusCheck.getRefundDetail().getTimeRefunded());
            statusMeta.setRefundedAmount(statusCheck.getRefundDetail().getRefundedAmount());
            statusMeta.setTransactionReference(statusCheck.getRefundDetail().getTransactionReference());

            statusCheckResponse.setStatusMessage(statusCheck.getStatusMessage());
            statusCheckResponse.setStatusMeta(statusMeta);

        } else if (detailInfo.equalsIgnoreCase("is-transaction-detail")) {
            statusMeta.setAmount(statusCheck.getTransactionDetail().getAmount());
            statusMeta.setCardholderName("");
            statusMeta.setCurrency(statusCheck.getTransactionDetail().getCurrency());
            statusMeta.setMask(statusCheck.getTransactionDetail().getMask());
            statusMeta.setPaymentDate(statusCheck.getTransactionDetail().getTimeIn());
            statusMeta.setRefundedAmount(null);
            statusMeta.setTransactionReference(statusCheck.getTransactionDetail().getTransactionReference());

            statusCheckResponse.setStatusMessage(statusCheck.getStatusMessage());
            statusCheckResponse.setStatusMeta(statusMeta);

        }

        return statusCheckResponse;

    }

}
