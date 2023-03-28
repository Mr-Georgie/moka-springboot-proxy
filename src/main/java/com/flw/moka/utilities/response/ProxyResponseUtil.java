package com.flw.moka.utilities.response;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.flw.moka.entity.constants.Methods;
import com.flw.moka.entity.request.ProductRequest;
import com.flw.moka.entity.response.ProviderResponse;
import com.flw.moka.entity.response.ProviderResponseData;
import com.flw.moka.entity.response.ProxyResponse;

@Component
public class ProxyResponseUtil {
    ProxyResponse proxyResponse = new ProxyResponse();

    public ProxyResponse setProxyResponse(Optional<ProviderResponseData> dataEntity,
            Optional<ProviderResponse> bodyEntity, ProductRequest productRequest, String method) {

        ProviderResponse providerResponse = bodyEntity.get();

        if (dataEntity.isPresent()) {
            formatSuccessfulResponseBasedOnMethod(method);
            proxyResponse.setTransactionReference(productRequest.getTransactionReference());
            proxyResponse.setExternalReference(dataEntity.get().getVirtualPosOrderId());
            proxyResponse.setProviderResponse(providerResponse);
            proxyResponse.setProvider("MOKA");
        } else {

            String exactProviderMessage = formatProviderResponseResultCode(providerResponse);

            proxyResponse.setMessage(method.toUpperCase() + " Failed: " + exactProviderMessage);
            proxyResponse.setCode("RR");
            proxyResponse.setTransactionReference(productRequest.getTransactionReference());
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
            proxyResponse.setMessage("Capture Successful");
            proxyResponse.setCode("00");
        } else if (method.equalsIgnoreCase(Methods.VOID)) {
            proxyResponse.setMessage("Void Successful");
            proxyResponse.setCode("01");
        } else if (method.equalsIgnoreCase(Methods.REFUND)) {
            proxyResponse.setMessage("Refund Successful");
            proxyResponse.setCode("03");
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
