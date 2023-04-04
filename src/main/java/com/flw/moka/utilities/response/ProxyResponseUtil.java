package com.flw.moka.utilities.response;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.flw.moka.entity.constants.Methods;
import com.flw.moka.entity.request.ProductRequest;
import com.flw.moka.entity.response.Meta;
import com.flw.moka.entity.response.ProviderResponse;
import com.flw.moka.entity.response.ProviderResponseData;
import com.flw.moka.entity.response.ProxyResponse;

@Component
public class ProxyResponseUtil {
    ProxyResponse proxyResponse = new ProxyResponse();
    Meta meta = new Meta();

    public ProxyResponse setProxyResponse(Optional<ProviderResponseData> dataEntity,
            Optional<ProviderResponse> bodyEntity, ProductRequest productRequest, String method) {

        ProviderResponse providerResponse = bodyEntity.get();

        if (dataEntity.isPresent()) {
            formatSuccessfulResponseBasedOnMethod(method);
            meta.setTransactionReference(productRequest.getTransactionReference());
            meta.setPayloadReference(productRequest.getPayloadReference());
            meta.setExternalReference(dataEntity.get().getVirtualPosOrderId());

            proxyResponse.setMeta(meta);
            proxyResponse.setProviderResponse(providerResponse);
            proxyResponse.getMeta().setProvider("MOKA");
        } else {

            String exactProviderMessage = formatProviderResponseResultCode(providerResponse);

            proxyResponse.setResponseMessage(method.toUpperCase() + " Failed: " + exactProviderMessage);
            proxyResponse.setResponseCode("RR");
            proxyResponse.getMeta().setTransactionReference(productRequest.getTransactionReference());
            proxyResponse.getMeta().setPayloadReference(productRequest.getPayloadReference());
            proxyResponse.getMeta().setProvider("MOKA");
            proxyResponse.setProviderResponse(providerResponse);
        }
        return proxyResponse;
    }

    private ProxyResponse formatSuccessfulResponseBasedOnMethod(String method) {
        if (method.equalsIgnoreCase(Methods.AUTHORIZE)) {
            proxyResponse.setResponseMessage("Pending Capture");
            proxyResponse.setResponseCode("02");
        } else if (method.equalsIgnoreCase(Methods.CAPTURE)) {
            proxyResponse.setResponseMessage("Capture Successful");
            proxyResponse.setResponseCode("00");
        } else if (method.equalsIgnoreCase(Methods.VOID)) {
            proxyResponse.setResponseMessage("Void Successful");
            proxyResponse.setResponseCode("01");
        } else if (method.equalsIgnoreCase(Methods.REFUND)) {
            proxyResponse.setResponseMessage("Refund Successful");
            proxyResponse.setResponseCode("03");
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
