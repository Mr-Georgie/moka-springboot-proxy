package com.flw.moka.utilities.helpers;

import org.springframework.stereotype.Component;

import com.flw.moka.entity.models.Logs;
import com.flw.moka.entity.request.ProductRequest;
import com.flw.moka.entity.response.ProxyResponse;
import com.flw.moka.service.entity_service.LogsService;
import com.google.gson.Gson;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class LogsUtil {

    LogsService logsService;

    public void setLogs(ProxyResponse proxyResponse, ProductRequest productRequest, String method) {

        TimeUtil timeUtility = new TimeUtil();
        Logs log = new Logs();
        Gson gson = new Gson();

        String jsonProductRequest = gson.toJson(productRequest);
        String jsonProxyResponse = gson.toJson(proxyResponse);

        log.setExternalReference(proxyResponse.getExternalReference());
        log.setBody(jsonProductRequest);
        log.setMethod(method);
        log.setResponse(jsonProxyResponse);
        log.setTransactionReference(productRequest.getTransactionReference());
        log.setTimeIn(timeUtility.getDateTime());

        logsService.saveLogs(log);
    }
}
