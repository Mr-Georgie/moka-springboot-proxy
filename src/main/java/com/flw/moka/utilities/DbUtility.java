package com.flw.moka.utilities;

import com.flw.moka.entity.CardParams;
import com.flw.moka.entity.helpers.ProductRequest;
import com.flw.moka.entity.helpers.ProxyResponse;
import com.google.gson.Gson;

public class DbUtility {

    String method;

    public DbUtility(String method) {
        this.method = method;
    }

    public CardParams setCardParams(ProxyResponse proxyResponse, ProductRequest productRequest) {

        TimeUtility timeUtility = new TimeUtility();
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

}
