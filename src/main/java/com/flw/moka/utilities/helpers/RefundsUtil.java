package com.flw.moka.utilities.helpers;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.flw.moka.entity.models.Refunds;
import com.flw.moka.entity.models.Transaction;
import com.flw.moka.entity.request.ProductRequest;
import com.flw.moka.entity.response.ProxyResponse;
import com.flw.moka.exception.TransactionMethodAlreadyDoneException;
import com.flw.moka.service.entity_service.RefundsService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class RefundsUtil {

    RefundsService refundsService;
    LogsUtil logsUtil;

    public void saveRefundToDataBase(ProxyResponse proxyResponse,
            Refunds refund, Transaction transaction) {

        TimeUtil timeUtility = new TimeUtil();

        if (refund.getAmount() == null) {
            refund.setAmount(transaction.getAmount());
        }

        refund.setResponseCode(proxyResponse.getCode());
        refund.setResponseMessage(proxyResponse.getMessage());
        refund.setProvider(proxyResponse.getProvider());

        refund.setCurrency(transaction.getCurrency());
        refund.setMask(transaction.getMask());
        refund.setTimeRefunded(timeUtility.getDateTime());
        refund.setTransactionReference(transaction.getTransactionReference());
        refund.setExternalReference(transaction.getExternalReference());
        refund.setNarration("CARD Transaction");
        refund.setCountry("TR");

        refundsService.saveRefund(refund);
    }

    public Refunds checkIfRefundExistInDB(ProductRequest productRequest, String method) {

        String transactionReference = productRequest.getTransactionReference();

        Optional<Refunds> refund = refundsService.getRefund(transactionReference);

        if (refund.isPresent()) {

            if (refund.get().getResponseCode() == "03") {
                ProxyResponse proxyResponse = prepareResponseIfTransactionDoesNotExist(transactionReference, method);
                proxyResponse.setMessage("This has been refunded");
                logsUtil.setLogs(proxyResponse, productRequest, method);

                throw new TransactionMethodAlreadyDoneException(proxyResponse.getMessage());
            }

            return refund.get();
        } else {
            Refunds newRefund = new Refunds();

            if (productRequest.getAmount() != null) {
                newRefund.setAmount(productRequest.getAmount());
            }

            return newRefund;
        }
    }

    static ProxyResponse prepareResponseIfTransactionDoesNotExist(String reference, String method) {
        ProxyResponse proxyResponse = new ProxyResponse();

        proxyResponse.setCode("RR");
        proxyResponse.setProvider("MOKA");

        return proxyResponse;
    }
}
