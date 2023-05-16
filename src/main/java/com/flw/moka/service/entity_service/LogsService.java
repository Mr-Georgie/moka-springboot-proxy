package com.flw.moka.service.entity_service;

import java.util.List;

import com.flw.moka.entity.models.Logs;
import com.flw.moka.entity.request.ProductRequest;
import com.flw.moka.entity.response.ProxyResponse;

public interface LogsService {

    void saveLogs(ProxyResponse proxyResponse, ProductRequest productRequest, String method);

    void saveAll(List<Logs> logsList);

    List<Logs> findAll();

    void deleteAll();

}
