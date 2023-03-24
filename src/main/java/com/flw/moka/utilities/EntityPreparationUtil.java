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
        transaction.setResponseCode(proxyResponse.getCode());
        transaction.setResponseMessage(proxyResponse.getMessage());

        if (proxyResponse.getExRef() != "null") {
            transaction.setExternalRef(proxyResponse.getExRef());
            transaction.setTransactionStatus(method.toUpperCase());

            if (method.equalsIgnoreCase(Methods.CAPTURE)) {
                transaction.setTimeCaptured(timeUtility.getDateTime());
            } else if (method.equalsIgnoreCase(Methods.REFUND)) {
                transaction.setTimeRefunded(timeUtility.getDateTime());
            } else if (method.equalsIgnoreCase(Methods.VOID)) {
                transaction.setTimeVoided(timeUtility.getDateTime());
            }
        }

        return transaction;
    }

    public Refunds setRefund(Transaction transaction, ProxyResponse proxyResponse) {

        Refunds refund = new Refunds();
        TimeUtil timeUtility = new TimeUtil();

        refund.setExternalRef(proxyResponse.getExRef());
        refund.setTransactionRef(transaction.getTransactionRef());
        refund.setResponseCode(proxyResponse.getCode());
        refund.setResponseMessage(proxyResponse.getMessage());
        refund.setTimeRefunded(timeUtility.getDateTime());
        refund.setAmount(transaction.getAmount());
        refund.setCountry(transaction.getCountry());
        refund.setCurrency(transaction.getCurrency());
        refund.setMask(transaction.getMask());
        refund.setNarration("Card transaction");
        refund.setProvider("Moka");

        return refund;
    }

}
