package com.flw.moka.service.helper_service;

import java.util.Optional;

import com.flw.moka.entity.request.ProductRequest;
import com.flw.moka.entity.response.ProviderResponse;
import com.flw.moka.entity.response.ProviderResponseData;
import com.flw.moka.entity.response.ProxyResponse;

public interface ProxyResponseService {
        ProxyResponse createProxyResponse(Optional<ProviderResponseData> dataEntity,
                        Optional<ProviderResponse> bodyEntity,
                        ProductRequest productRequest, String method);

        // void saveFailedResponseToDB(ProxyResponse proxyResponse,
        // String transactionRef, String method);

        // public void sendMethodAlreadyDoneResponse(String transactionCurrentStatus,
        // String transactionRef);
}
