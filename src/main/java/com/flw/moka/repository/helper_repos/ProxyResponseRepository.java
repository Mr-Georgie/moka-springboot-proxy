package com.flw.moka.repository.helper_repos;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.flw.moka.entity.helpers.Methods;
import com.flw.moka.entity.helpers.ProductRequest;
import com.flw.moka.entity.helpers.ProviderResponse;
import com.flw.moka.entity.helpers.ProviderResponseData;
import com.flw.moka.entity.helpers.ProxyResponse;

@Repository
public class ProxyResponseRepository {
    ProxyResponse proxyResponse = new ProxyResponse();

    public ProxyResponse setProxyResponse(Optional<ProviderResponseData> dataEntity,
            Optional<ProviderResponse> bodyEntity, ProductRequest productRequest, String method) {

        ProviderResponse providerResponse = bodyEntity.get();

        if (dataEntity.isPresent()) {
            formatSuccessfulResponseBasedOnMethod(method);
            proxyResponse.setTxRef(productRequest.getTransactionReference());
            proxyResponse.setExRef(dataEntity.get().getVirtualPosOrderId());
            proxyResponse.setProviderResponse(providerResponse);
            proxyResponse.setProvider("MOKA");
        } else {

            String exactProviderMessage = formatProviderFailedResponseResultCode(providerResponse);

            proxyResponse.setMessage(exactProviderMessage);
            proxyResponse.setCode("RR - " + method.toUpperCase() + " Failed");
            proxyResponse.setTxRef(productRequest.getTransactionReference());
            proxyResponse.setExRef("null");
            proxyResponse.setProvider("MOKA");
            proxyResponse.setProviderResponse(providerResponse);
        }
        return proxyResponse;
    }

    private ProxyResponse formatSuccessfulResponseBasedOnMethod(String method) {
        if (method.equalsIgnoreCase(Methods.AUTHORIZE)) {
            proxyResponse.setMessage("Pending Capture");
            proxyResponse.setCode("02");
        } else if (method.equalsIgnoreCase(Methods.CAPTURE)) {
            proxyResponse.setMessage("Successful");
            proxyResponse.setCode("00");
        } else if (method.equalsIgnoreCase(Methods.VOID)) {
            proxyResponse.setMessage("Successful");
            proxyResponse.setCode("00 - Voided");
        } else if (method.equalsIgnoreCase(Methods.REFUND)) {
            proxyResponse.setMessage("Successful");
            proxyResponse.setCode("00 - Refunded");
        }

        return proxyResponse;
    }

    private String formatProviderFailedResponseResultCode(ProviderResponse providerResponseOject) {

        String resultCode = providerResponseOject.getResultCode();

        String[] eachWordInResponse = resultCode.split("\\.");

        String lastWordsInResponse = eachWordInResponse[eachWordInResponse.length - 1];

        String formattedResponse = lastWordsInResponse.replaceAll("([a-z])([A-Z])", "$1 $2");

        return formattedResponse;
    }
}
