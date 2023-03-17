package com.flw.moka.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.flw.moka.entity.ProductRequest;
import com.flw.moka.entity.ProviderResponse;
import com.flw.moka.entity.ProviderResponseData;
import com.flw.moka.entity.ProxyResponse;

@Repository
public class ProxyResponseRepository {
    ProxyResponse proxyResponse = new ProxyResponse();

    public ProxyResponse setProxyResponse(Optional<ProviderResponseData> dataEntity,
            Optional<ProviderResponse> bodyEntity, ProductRequest productRequest) {

        ProviderResponse providerResponse = bodyEntity.get();

        if (dataEntity.isPresent()) {
            proxyResponse.setMessage("Successful");
            proxyResponse.setCode("00");
            proxyResponse.setTxRef(productRequest.getTransactionReference());
            proxyResponse.setExRef(dataEntity.get().getVirtualPosOrderId());
            proxyResponse.setProviderResponse(providerResponse);
            proxyResponse.setProvider("MOKA");
        } else {

            String exactProviderMessage = formatProviderResponseResultCode(providerResponse);

            proxyResponse.setMessage(exactProviderMessage);
            proxyResponse.setCode("RR-400");
            proxyResponse.setTxRef(productRequest.getTransactionReference());
            proxyResponse.setExRef("null");
            proxyResponse.setProvider("MOKA");
            proxyResponse.setProviderResponse(providerResponse);
        }
        return proxyResponse;
    }

    private String formatProviderResponseResultCode(ProviderResponse providerResponseOject) {

        String resultCode = providerResponseOject.getResultCode();

        String[] eachWordInResponse = resultCode.split("\\.");

        String lastWordsInResponse = eachWordInResponse[eachWordInResponse.length - 1];

        String formattedResponse = lastWordsInResponse.replaceAll("([a-z])([A-Z])", "$1 $2");

        return formattedResponse;
    }
}
