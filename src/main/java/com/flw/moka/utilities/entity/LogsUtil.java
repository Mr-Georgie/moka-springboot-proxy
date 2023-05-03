package com.flw.moka.utilities.entity;

import org.springframework.stereotype.Component;

import com.flw.moka.entity.models.Logs;
import com.flw.moka.entity.request.ProductRequest;
import com.flw.moka.entity.response.ProxyResponse;
import com.flw.moka.service.entity_service.LogsService;
import com.flw.moka.utilities.helpers.TimeUtil;
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

        log.setExternalReference(proxyResponse.getMeta().getExternalReference());
        log.setBody(jsonProductRequest);
        log.setMethod(method);
        log.setResponse(jsonProxyResponse);
        log.setTransactionReference(productRequest.getTransactionReference());
        log.setTimeIn(timeUtility.getDateTime());

        logsService.saveLogs(log);
    }
}
