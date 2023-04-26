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

        Optional<Refunds> findRefund = refundsService.findLastTransactionOccurrence(reference);

        if ((findRefund.isPresent() && !findRefund.get().getResponseCode().equalsIgnoreCase("03"))) {

            StatusCheck providerStatusCheck = getStatusFromProvider(providerPayload,
                    productRequest);

            logStatusCheck(providerStatusCheck);
            return setStatusCheckResponseMeta(providerStatusCheck, "is-provider-detail");

        } else if ((findRefund.isPresent() && findRefund.get().getResponseCode().equalsIgnoreCase("03"))) {
            Refunds refund = findRefund.get();
            String status = "REFUNDED";
            statusCheck.setRefund(refund);
            statusCheck.setStatusMessage(status);

            logStatusCheck(statusCheck);
            return setStatusCheckResponseMeta(statusCheck, "is-refund-detail");

        } else {
            // To allow status check work for references not present in database
            Optional<Transaction> findTransaction = transactionService.getTransactionIfExistInDB(reference);

            String responseCode = findTransaction.get().getResponseCode();

            if (findTransaction.isPresent() && !responseCode.equalsIgnoreCase("RR")) {
                Transaction transaction = findTransaction.get();
                String status = transaction.getTransactionStatus();
                statusCheck.setTransaction(transaction);
                statusCheck.setStatusMessage(
                        status.equalsIgnoreCase(Methods.VOID) ? "VOIDED" : status + "D");

                logStatusCheck(statusCheck);
                return setStatusCheckResponseMeta(statusCheck, "is-transaction-detail");
            } else {
                StatusCheck providerStatusCheck = getStatusFromProvider(providerPayload,
                        productRequest);
                logStatusCheck(providerStatusCheck);

                return setStatusCheckResponseMeta(providerStatusCheck, "is-provider-detail");
            }
        }

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

        Boolean isAwaitingPaymentConfirmation = (paymentStatus == 0 && transactionStatus == 0);
        Boolean isPreProvisionSuccessful = (paymentStatus == 1 && transactionStatus == 1);
        Boolean isPreProvisionFailed = (paymentStatus == 1 && transactionStatus == 2);
        Boolean isPaymentSuccessful = (paymentStatus == 2 && transactionStatus == 1);
        Boolean isPaymentFailed = (paymentStatus == 2 && transactionStatus == 2);
        Boolean isCancellationSuccessful = (paymentStatus == 3 && transactionStatus == 1);
        Boolean isRefundSuccessful = (paymentStatus == 4 && transactionStatus == 1);

        if (isAwaitingPaymentConfirmation) {
            statusCheck.setStatusMessage("Payment Authorization Pending");
        } else if (isPreProvisionSuccessful) {
            statusCheck.setStatusMessage("Authorize Successful");
        } else if (isPreProvisionFailed) {
            statusCheck.setStatusMessage("Authorize Failed");
        } else if (isPaymentSuccessful) {
            statusCheck.setStatusMessage("Payment Successful");
        } else if (isPaymentFailed) {
            statusCheck.setStatusMessage("Payment Failed");
        } else if (isCancellationSuccessful) {
            statusCheck.setStatusMessage("Void Successful");
        } else if (isRefundSuccessful) {
            statusCheck.setStatusMessage("Refund Successful");
        } else {
            statusCheck.setStatusMessage("Invalid Payment Status");
        }

        statusCheck.setPaymentDetail(paymentDetail);

        return statusCheck;
    }

    private void logStatusCheck(StatusCheck statusCheck) {
        TimeUtil timeUtility = new TimeUtil();
        Logs log = new Logs();
        Gson gson = new Gson();

        String jsonStatusCheck = gson.toJson(statusCheck);

        log.setExternalReference("N/A");
        log.setBody("Status check");
        log.setMethod("status");
        log.setResponse(jsonStatusCheck);
        log.setTransactionReference("N/A");
        log.setTimeIn(timeUtility.getDateTime());

        logsService.saveLogs(log);
    }

    private StatusCheckResponse setStatusCheckResponseMeta(StatusCheck statusCheck, String detailInfo) {
        StatusCheckResponse statusCheckResponse = new StatusCheckResponse();
        StatusMeta statusMeta = new StatusMeta();

        switch (detailInfo.toLowerCase()) {
            case "is-provider-detail":
                PaymentDetail paymentDetail = statusCheck.getPaymentDetail();
                statusMeta.setAmount(Long.valueOf(paymentDetail.getAmount()));
                statusMeta.setCardholderName(paymentDetail.getCardHolderFullName());
                statusMeta.setCurrency(paymentDetail.getCurrencyCode());
                statusMeta.setPaymentDate(paymentDetail.getPaymentDate());
                statusMeta.setRefundedAmount(Long.valueOf(paymentDetail.getRefAmount()));
                statusMeta.setTransactionReference(paymentDetail.getOtherTrxCode());
                break;
            case "is-refund-detail":
                Refunds refundDetail = statusCheck.getRefund();
                statusMeta.setAmount(refundDetail.getBalance());
                statusMeta.setCardholderName("");
                statusMeta.setCurrency(refundDetail.getCurrency());
                statusMeta.setMask(refundDetail.getMask());
                statusMeta.setPaymentDate(refundDetail.getTimeRefunded());
                statusMeta.setRefundedAmount(refundDetail.getRefundedAmount());
                statusMeta.setTransactionReference(refundDetail.getTransactionReference());
                break;
            case "is-transaction-detail":
                Transaction transactionDetail = statusCheck.getTransaction();
                if (statusCheck.getStatusMessage().equalsIgnoreCase("VOIDED")) {
                    statusMeta.setAmount(Long.valueOf(0));
                    statusMeta.setRefundedAmount(transactionDetail.getAmount());
                } else {
                    statusMeta.setAmount(transactionDetail.getAmount());
                }
                statusMeta.setCardholderName("");
                statusMeta.setCurrency(transactionDetail.getCurrency());
                statusMeta.setMask(transactionDetail.getMask());
                statusMeta.setPaymentDate(transactionDetail.getTimeIn());
                statusMeta.setTransactionReference(transactionDetail.getTransactionReference());
                break;
            default:
                throw new IllegalArgumentException("Invalid detailInfo value: " + detailInfo);
        }

        statusCheckResponse.setStatusMessage(statusCheck.getStatusMessage());
        statusCheckResponse.setStatusMeta(statusMeta);
        return statusCheckResponse;
    }

    // Optional<Transaction> findTransaction = Optional.ofNullable(transactionService
    //         .getTransaction(productRequest, Methods.STATUS));

}
