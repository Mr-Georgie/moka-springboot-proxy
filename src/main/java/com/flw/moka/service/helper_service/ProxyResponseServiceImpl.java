package com.flw.moka.service.helper_service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.flw.moka.entity.request.ProductRequest;
import com.flw.moka.entity.response.ProviderResponse;
import com.flw.moka.entity.response.ProviderResponseData;
import com.flw.moka.entity.response.ProxyResponse;
import com.flw.moka.repository.LogsRepository;
import com.flw.moka.utilities.response.ProxyResponseUtil;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class ProxyResponseServiceImpl implements ProxyResponseService {

    ProxyResponseUtil proxyResponseRepository;
    LogsRepository logsRepository;

    @Override
    public ProxyResponse createProxyResponse(Optional<ProviderResponseData> dataEntity,
            Optional<ProviderResponse> bodyEntity, ProductRequest productRequest, String method) {
        return proxyResponseRepository.setProxyResponse(dataEntity, bodyEntity, productRequest, method);
    }

}
