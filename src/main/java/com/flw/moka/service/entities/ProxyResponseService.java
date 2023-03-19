package com.flw.moka.service.entities;

import java.util.Optional;

import com.flw.moka.entity.helpers.ProductRequest;
import com.flw.moka.entity.helpers.ProviderResponse;
import com.flw.moka.entity.helpers.ProviderResponseData;
import com.flw.moka.entity.helpers.ProxyResponse;

public interface ProxyResponseService {
    ProxyResponse createProxyResponse(Optional<ProviderResponseData> dataEntity, Optional<ProviderResponse> bodyEntity,
            ProductRequest productRequest);

    void saveFailedResponseToDB(ProxyResponse proxyResponse,
            String transactionRef, String method);
}
