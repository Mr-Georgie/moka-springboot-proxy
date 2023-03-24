package com.flw.moka.utilities;

import com.flw.moka.entity.CardParams;
import com.flw.moka.entity.Refunds;
import com.flw.moka.entity.Transaction;
import com.flw.moka.entity.helpers.Methods;
import com.flw.moka.entity.helpers.ProductRequest;
import com.flw.moka.entity.helpers.ProxyResponse;
import com.google.gson.Gson;

public class EntityPreparationUtil {

    String method;

    public EntityPreparationUtil(String method) {
        this.method = method;
    }

    public CardParams setCardParams(ProxyResponse proxyResponse, ProductRequest productRequest) {

        TimeUtil timeUtility = new TimeUtil();
        CardParams cardParams = new CardParams();
        Gson gson = new Gson();

        String jsonProductRequest = gson.toJson(productRequest);
        String jsonProxyResponse = gson.toJson(proxyResponse);

        cardParams.setExternalRef(proxyResponse.getExRef());
        cardParams.setBody(jsonProductRequest);
        cardParams.setMethod(method);
        cardParams.setResponse(jsonProxyResponse);
        cardParams.setTransactionRef(proxyResponse.getTxRef());
        cardParams.setTimeIn(timeUtility.getDateTime());

        return cardParams;
    }

    public Transaction updateTransactionStatus(ProductRequest productRequest, Transaction transaction,
            ProxyResponse proxyResponse) {

        TimeUtil timeUtility = new TimeUtil();

        transaction.setTransactionRef(productRequest.getTransactionReference());
        transaction.setTransactionStatus(method.toUpperCase());

        if (method.equalsIgnoreCase(Methods.CAPTURE)) {
            transaction.setResponseCode("00");
            transaction.setResponseMessage("Successful");
            transaction.setTimeCaptured(timeUtility.getDateTime());
        } else if (method.equalsIgnoreCase(Methods.REFUND)) {
            transaction.setResponseCode("00 - Refunded");
            transaction.setResponseMessage("Transaction refunded successfully");
            transaction.setTimeRefunded(timeUtility.getDateTime());
        } else if (method.equalsIgnoreCase(Methods.VOID)) {
            transaction.setResponseCode("00 - Voided");
            transaction.setResponseMessage("Transaction voided successfully");
            transaction.setTimeVoided(timeUtility.getDateTime());
        }

        if (proxyResponse.getExRef() != null) {
            transaction.setExternalRef(proxyResponse.getExRef());
        }

        return transaction;
    }

    public Refunds setRefund(ProductRequest productRequest,
            ProxyResponse proxyResponse) {

        Refunds refund = new Refunds();
        TimeUtil timeUtility = new TimeUtil();

        refund.setExternalRef(proxyResponse.getExRef());
        refund.setTransactionRef(productRequest.getTransactionReference());
        refund.setResponseCode("00");
        refund.setResponseMessage("Transaction refunded successfully");
        refund.setTimeRefunded(timeUtility.getDateTime());
        refund.setAmount(productRequest.getAmount());
        refund.setCountry(productRequest.getCountry());
        refund.setCurrency(productRequest.getCurrency());
        refund.setMask(productRequest.getCardNo());
        refund.setNarration("Card transaction");
        refund.setProvider("Moka");

        return refund;
    }

}
