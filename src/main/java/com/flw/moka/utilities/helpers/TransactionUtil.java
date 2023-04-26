package com.flw.moka.utilities.helpers;

import org.springframework.stereotype.Component;

import com.flw.moka.entity.constants.Methods;
import com.flw.moka.entity.models.Transaction;
import com.flw.moka.entity.request.ProductRequest;
import com.flw.moka.entity.response.Meta;
import com.flw.moka.entity.response.ProxyResponse;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class TransactionUtil {

    public void setAuthorizeTransactionFields(Transaction transaction, ProductRequest productRequest,
        ProxyResponse proxyResponse, TimeUtil timeUtility) {
        transaction.setAmount(productRequest.getAmount());
        transaction.setCountry("TR");
        transaction.setCurrency(productRequest.getCurrency());
        transaction.setMask(productRequest.getCardNumber());
        transaction.setTimeIn(timeUtility.getDateTime());
        transaction.setPayloadReference(productRequest.getPayloadReference());
        transaction.setTransactionReference(proxyResponse.getMeta().getTransactionReference());
        transaction.setNarration("CARD Transaction");

        String externalReference = proxyResponse.getMeta().getExternalReference();
        if (externalReference != null) {
            transaction.setExternalReference(externalReference);
            transaction.setTransactionStatus(Methods.AUTHORIZE.toUpperCase());
        }
    }

    public void setCaptureTransactionFields(Transaction transaction, ProxyResponse proxyResponse,
            TimeUtil timeUtility) {
        String externalReference = proxyResponse.getMeta().getExternalReference();
        if (externalReference != null) {
            transaction.setExternalReference(externalReference);
            transaction.setTransactionStatus(Methods.CAPTURE.toUpperCase());
            transaction.setTimeCaptured(timeUtility.getDateTime());
        }
    }

    public void setVoidTransactionFields(Transaction transaction, ProxyResponse proxyResponse,
            TimeUtil timeUtility) {
        String externalReference = proxyResponse.getMeta().getExternalReference();
        if (externalReference != null) {
            transaction.setExternalReference(externalReference);
            transaction.setTransactionStatus(Methods.VOID.toUpperCase());
            transaction.setTimeVoided(timeUtility.getDateTime());
        }
    }

    public void setRefundTransactionFields(Transaction transaction, ProxyResponse proxyResponse,
            TimeUtil timeUtility) {
        String externalReference = proxyResponse.getMeta().getExternalReference();
        if (externalReference != null) {
            transaction.setExternalReference(externalReference);
            transaction.setTimeVoided(timeUtility.getDateTime());
        }
    }

    public Transaction prepareNonExistingTransaction(ProductRequest productRequest, TimeUtil timeUtility) {
        Transaction transaction = new Transaction();
        transaction.setAmount(productRequest.getAmount());
        transaction.setCountry("TR");
        transaction.setCurrency(productRequest.getCurrency());
        transaction.setMask(productRequest.getCardNumber());
        transaction.setTimeIn(timeUtility.getDateTime());
        transaction.setTransactionReference(productRequest.getTransactionReference());
        transaction.setNarration("CARD Transaction");
        return transaction;
    }

    public ProxyResponse prepareResponseIfTransactionDoesNotExist(String reference, String method) {
        ProxyResponse proxyResponse = new ProxyResponse();
        Meta meta = new Meta();

        String message = String.format("Transaction with reference %s does not exist for method %s", reference, method);
        proxyResponse
                .setResponseMessage(message);
        proxyResponse.setResponseCode("RR");
        meta.setProvider("MOKA");
        proxyResponse.setMeta(meta);

        return proxyResponse;
    }
}
