package com.flw.moka.service.entity_service;

import java.util.List;

import com.flw.moka.entity.request.ProductRequest;
import com.flw.moka.entity.response.ProxyResponse;
import com.flw.moka.utilities.helpers.TimeUtil;
import com.google.gson.Gson;
import org.springframework.stereotype.Service;

import com.flw.moka.entity.models.Logs;
import com.flw.moka.repository.LogsRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class LogsServiceImpl implements LogsService {

    LogsRepository logsRepository;

    @Override
    public void saveLogs(ProxyResponse proxyResponse, ProductRequest productRequest, String method) {

        TimeUtil timeUtility = new TimeUtil();
        Logs logs = new Logs();
        Gson gson = new Gson();

        String jsonProductRequest = gson.toJson(productRequest);
        String jsonProxyResponse = gson.toJson(proxyResponse);

        logs.setExternalReference(proxyResponse.getMeta().getExternalReference());
        logs.setBody(jsonProductRequest);
        logs.setMethod(method);
        logs.setResponse(jsonProxyResponse);
        logs.setTransactionReference(productRequest.getTransactionReference());
        logs.setTimeIn(timeUtility.getDateTime());

        logsRepository.save(logs);
    }

    @Override
    public void saveAll(List<Logs> logsList) {
        logsRepository.saveAll(logsList);
    }

    @Override
    public List<Logs> findAll() {
        return logsRepository.findAll();
    }

    @Override
    public void deleteAll() {
        logsRepository.deleteAll();
    }
}
