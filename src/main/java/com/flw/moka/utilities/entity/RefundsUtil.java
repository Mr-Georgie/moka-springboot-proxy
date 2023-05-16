package com.flw.moka.utilities.entity;

import com.flw.moka.entity.constants.Methods;
import com.flw.moka.entity.models.Transaction;
import com.flw.moka.entity.request.ProductRequest;
import com.flw.moka.entity.response.ProxyResponse;
import com.flw.moka.utilities.helpers.TimeUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class RefundsUtil {

    public Transaction createRefundRecord(Transaction transaction, ProxyResponse proxyResponse, ProductRequest productRequest,
                                                TimeUtil timeUtility){
        Transaction refundedTransaction = new Transaction();

        String refundId = proxyResponse.getMeta().getRefundId();
        if (refundId != null) {
            refundedTransaction.setTransactionStatus(Methods.REFUND.toUpperCase());
            refundedTransaction.setTimeRefunded(timeUtility.getDateTime());
            refundedTransaction.setRefundId(proxyResponse.getMeta().getRefundId());
            refundedTransaction.setAmountRefunded(computeAmountRefunded(transaction, productRequest));
            refundedTransaction.setBalance(computeBalance(transaction, productRequest));

            // Set other properties from the original transaction
            refundedTransaction.setTransactionReference(transaction.getTransactionReference());
            refundedTransaction.setPayloadReference(transaction.getPayloadReference());
            refundedTransaction.setExternalReference(transaction.getExternalReference());
            refundedTransaction.setMask(transaction.getMask());
            refundedTransaction.setTimeIn(transaction.getTimeIn());
            refundedTransaction.setTimeCaptured(transaction.getTimeCaptured());
            refundedTransaction.setTimeVoided(transaction.getTimeVoided());
            refundedTransaction.setProvider(transaction.getProvider());
            refundedTransaction.setAmount(transaction.getAmount());
            refundedTransaction.setCurrency(transaction.getCurrency());
            refundedTransaction.setCountry(transaction.getCountry());
            refundedTransaction.setNarration(transaction.getNarration());
            refundedTransaction.setResponseMessage(transaction.getResponseMessage());
            refundedTransaction.setResponseCode(transaction.getResponseCode());
        }

        return refundedTransaction;
    }


    public Long computeBalance(Transaction transaction, ProductRequest productRequest) {

        long newBalance;

        if (productRequest.getAmount() == null) {
            newBalance = 0L;
        } else {

            if(productRequest.getAmount() > transaction.getAmount()) {
                return transaction.getBalance();
            }

            newBalance = transaction.getBalance() - productRequest.getAmount();
        }

        return newBalance;
    }

    public Long computeAmountRefunded(Transaction transaction, ProductRequest productRequest) {

        Long newRefundAmount;

        if (productRequest.getAmount() == null) {
            newRefundAmount = transaction.getAmount();
        } else {
            newRefundAmount = productRequest.getAmount() + transaction.getAmountRefunded();
        }

        if(newRefundAmount > transaction.getAmount()) {
            return transaction.getAmountRefunded();
        }

        return newRefundAmount;
    }
}
