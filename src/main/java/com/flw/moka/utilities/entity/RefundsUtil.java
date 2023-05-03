package com.flw.moka.utilities.entity;

import org.springframework.stereotype.Component;

import com.flw.moka.entity.models.Refunds;
import com.flw.moka.entity.models.Transaction;
import com.flw.moka.entity.request.ProductRequest;
import com.flw.moka.entity.response.Meta;
import com.flw.moka.entity.response.ProxyResponse;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class RefundsUtil {

    public Refunds updateRefund(ProxyResponse proxyResponse, Refunds refund, Transaction transaction) {
        Refunds newRefund = new Refunds();
        Long refundedAmount = refund.getRefundedAmount();
        Long currentBalance = refund.getBalance();
        Long updatedBalance;
        
        if (transaction == null) {
            newRefund.setRefundedAmount(refundedAmount);
            newRefund.setBalance(currentBalance);
            return newRefund;
        }

        if (proxyResponse.getResponseCode().equalsIgnoreCase("RR")) {
            refundedAmount = 0L;
            updatedBalance = (currentBalance == null) ? transaction.getAmount() : currentBalance;
        } else {
            if (refundedAmount == null) {
                refundedAmount = transaction.getAmount();
            }
            if (currentBalance == null || currentBalance != 0) {
                updatedBalance = transaction.getAmount() - refundedAmount;
            } else {
                updatedBalance = 0L;
            }
        }

        newRefund.setRefundedAmount(refundedAmount);
        newRefund.setBalance(updatedBalance);
        return newRefund;
    }

    public Refunds computeRefundedAmount(Refunds refund, ProductRequest productRequest) {

        Long requestedAmount = 0L;

        if (productRequest.getAmount() == null) {
            requestedAmount = refund.getBalance();
        } else {
            requestedAmount = productRequest.getAmount();
        }

        if(requestedAmount > refund.getBalance()) {
            // is refund request amount more than remaining balance? return null
            return null;
        }

        Long updateRefundedAmount = requestedAmount + refund.getRefundedAmount();

        return createNewComputedRefund(updateRefundedAmount, refund);
    }

    public ProxyResponse prepareFailedResponse(String reference, String method, String message) {
        ProxyResponse proxyResponse = new ProxyResponse();
        Meta meta = new Meta();

        meta.setProvider("MOKA");
        proxyResponse.setResponseCode("RR");
        proxyResponse.setMeta(meta);
        proxyResponse.setResponseMessage(message);

        return proxyResponse;
    }

    private Refunds createNewComputedRefund(Long updateRefundedAmount, Refunds existingRefund){
        Refunds newRefund = new Refunds();

        newRefund.setRefundedAmount(updateRefundedAmount);
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

    public ProxyResponse checkFoundRefundBalanceAndReturnResponse(Refunds foundRefund, ProductRequest productRequest, String method){
        if (foundRefund.getBalance() == 0) {
            String message = "This has been refunded";
            ProxyResponse proxyResponse = prepareFailedResponse(productRequest.getTransactionReference(), method, message);
            return proxyResponse;
        }
        return null;
    }

}