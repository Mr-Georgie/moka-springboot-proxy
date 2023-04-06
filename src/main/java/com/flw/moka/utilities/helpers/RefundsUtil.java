package com.flw.moka.utilities.helpers;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.flw.moka.entity.models.Refunds;
import com.flw.moka.entity.models.Transaction;
import com.flw.moka.entity.request.ProductRequest;
import com.flw.moka.entity.response.Meta;
import com.flw.moka.entity.response.ProxyResponse;
import com.flw.moka.exception.TransactionMethodAlreadyDoneException;
import com.flw.moka.service.entity_service.RefundsEntityService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class RefundsUtil {

    RefundsEntityService refundsService;
    LogsUtil logsUtil;
    GenerateReferenceUtil generateReferenceUtil;

    public void saveRefundToDataBase(ProxyResponse proxyResponse,
            Refunds existingRefund, Transaction transaction) {

        TimeUtil timeUtility = new TimeUtil();

        Refunds refund = computeBalance(proxyResponse, existingRefund, transaction);

        refund.setResponseCode(proxyResponse.getResponseCode());
        refund.setResponseMessage(proxyResponse.getResponseMessage());
        refund.setProvider(proxyResponse.getMeta().getProvider());

        refund.setCurrency(transaction.getCurrency());
        refund.setMask(transaction.getMask());
        refund.setTimeRefunded(timeUtility.getDateTime());
        refund.setTransactionReference(transaction.getTransactionReference());
        refund.setPayloadReference(transaction.getPayloadReference());
        refund.setRefundReference(generateReferenceUtil.generateRandom("REF"));
        refund.setExternalReference(transaction.getExternalReference());
        refund.setNarration("CARD Transaction");
        refund.setCountry("TR");
        refund.setLastRefund(true);

        Meta meta = new Meta();

        meta.setProvider(proxyResponse.getMeta().getProvider());
        meta.setTransactionReference(refund.getRefundReference());
        proxyResponse.setMeta(meta);

        refundsService.saveRefund(refund);
    }

    public Refunds checkIfRefundExistInDB(ProductRequest productRequest, String method) {

        String refundReference = productRequest.getTransactionReference();

        Optional<Refunds> findRefund = refundsService.getRefundByRefundReference(refundReference);

        if (findRefund.isPresent()) {

            if (findRefund.get().getResponseCode().equals("03") && findRefund.get().getBalance() == 0) {
                ProxyResponse proxyResponse = prepareResponseIfSuccessfulRefundsExist(refundReference, method);
                proxyResponse.setResponseMessage("This has been refunded");
                logsUtil.setLogs(proxyResponse, productRequest, method);

                throw new TransactionMethodAlreadyDoneException(proxyResponse.getResponseMessage());
            }

            if (findRefund.get().getLastRefund() == false) {
                ProxyResponse proxyResponse = prepareResponseIfSuccessfulRefundsExist(refundReference, method);
                proxyResponse.setResponseMessage("Please provide the latest refund reference");
                logsUtil.setLogs(proxyResponse, productRequest, method);

                throw new TransactionMethodAlreadyDoneException(proxyResponse.getResponseMessage());
            }

            Refunds existingRefund = computeRefundedAmount(findRefund, productRequest);

            return existingRefund;

        } else {

            Optional<Refunds> findRefundByTransactionReference = refundsService
                    .getRefundByTransactionReference(refundReference);

            if (findRefundByTransactionReference.isPresent()) {

                if (findRefundByTransactionReference.get().getBalance() == 0) {
                    ProxyResponse proxyResponse = prepareResponseIfSuccessfulRefundsExist(refundReference, method);
                    proxyResponse.setResponseMessage("This has been refunded");
                    logsUtil.setLogs(proxyResponse, productRequest, method);

                    throw new TransactionMethodAlreadyDoneException(proxyResponse.getResponseMessage());
                }

                ProxyResponse proxyResponse = prepareResponseIfSuccessfulRefundsExist(refundReference, method);
                proxyResponse.setResponseMessage("Please provide the refund reference for this transaction");
                logsUtil.setLogs(proxyResponse, productRequest, method);

                throw new TransactionMethodAlreadyDoneException(proxyResponse.getResponseMessage());
            }

            Refunds refund = new Refunds();

            if (productRequest.getAmount() != null) {
                refund.setRefundedAmount(productRequest.getAmount());
            }

            refund.setRefundReference(refundReference);
            refund.setResponseCode("RR");
            refund.setResponseMessage("Initiating refund...");

            return refund;
        }
    }

    private Refunds computeBalance(ProxyResponse proxyResponse, Refunds refund, Transaction transaction) {

        Refunds newRefund = new Refunds();

        if (!proxyResponse.getResponseCode().equalsIgnoreCase("RR")) {
            if (refund.getRefundedAmount() == null)
                newRefund.setRefundedAmount(transaction.getAmount());
            else
                newRefund.setRefundedAmount(refund.getRefundedAmount());

            if (refund.getBalance() == null || refund.getBalance() != 0) {
                Long balance = transaction.getAmount() - newRefund.getRefundedAmount();
                newRefund.setBalance(balance);
            }

            return newRefund;
        }

        newRefund.setBalance(transaction.getAmount());

        return newRefund;
    }

    private Refunds computeRefundedAmount(Optional<Refunds> findRefund, ProductRequest productRequest) {
        Refunds existingRefund = findRefund.get();

        Refunds newRefund = new Refunds();

        Long totalAmountRefundable = existingRefund.getRefundedAmount() +
                existingRefund.getBalance();

        if (totalAmountRefundable.equals(existingRefund.getRefundedAmount())) {
            return findRefund.get();
        }

        Long updatedRefundAmount = productRequest.getAmount() +
                existingRefund.getRefundedAmount();

        existingRefund.setLastRefund(false);

        newRefund.setRefundedAmount(updatedRefundAmount);
        newRefund.setResponseCode(existingRefund.getResponseCode());
        newRefund.setResponseMessage(existingRefund.getResponseMessage());
        newRefund.setProvider(existingRefund.getProvider());

        newRefund.setCurrency(existingRefund.getCurrency());
        newRefund.setMask(existingRefund.getMask());
        newRefund.setTimeRefunded(existingRefund.getTimeRefunded());
        newRefund.setTransactionReference(existingRefund.getTransactionReference());
        newRefund.setRefundReference(existingRefund.getRefundReference());
        newRefund.setExternalReference(existingRefund.getExternalReference());
        newRefund.setNarration(existingRefund.getNarration());
        newRefund.setCountry(existingRefund.getCountry());

        return newRefund;
    }

    private ProxyResponse prepareResponseIfSuccessfulRefundsExist(String reference, String method) {
        ProxyResponse proxyResponse = new ProxyResponse();
        Meta meta = new Meta();

        meta.setProvider("MOKA");
        proxyResponse.setResponseCode("RR");
        proxyResponse.setMeta(meta);

        return proxyResponse;
    }
}