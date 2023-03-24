package com.flw.moka.service.helper_service;

import java.util.Optional;

import com.flw.moka.entity.helpers.ProductRequest;
import com.flw.moka.entity.helpers.ProviderResponse;
import com.flw.moka.entity.helpers.ProviderResponseData;
import com.flw.moka.entity.helpers.ProxyResponse;

public interface ProxyResponseService {
        ProxyResponse createProxyResponse(Optional<ProviderResponseData> dataEntity,
                        Optional<ProviderResponse> bodyEntity,
                        ProductRequest productRequest, String method);

        void saveFailedResponseToDB(ProxyResponse proxyResponse,
                        String transactionRef, String method);

        public <T> T sendMethodAlreadyDoneResponse(String transactionCurrentStatus, String transactionRef,
                        Class<T> returnType);
}
