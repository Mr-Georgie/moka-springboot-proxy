package com.flw.moka.service.entities;

import java.util.Optional;

import com.flw.moka.entity.ProductRequest;
import com.flw.moka.entity.ProviderResponse;
import com.flw.moka.entity.ProviderResponseData;
import com.flw.moka.entity.ProxyResponse;

public interface ProxyResponseService {
    ProxyResponse createProxyResponse(Optional<ProviderResponseData> dataEntity, Optional<ProviderResponse> bodyEntity,
            ProductRequest productRequest);

    void saveFailedResponseToDB(ProxyResponse proxyResponse,
            String transactionRef, String method);
}
