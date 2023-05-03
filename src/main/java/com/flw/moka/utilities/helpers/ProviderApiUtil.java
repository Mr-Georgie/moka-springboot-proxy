package com.flw.moka.utilities.helpers;

import java.net.URI;
import java.util.Optional;

import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.flw.moka.entity.request.ProductRequest;
import com.flw.moka.entity.request.ProviderPayload;
import com.flw.moka.entity.response.ProviderResponse;
import com.flw.moka.entity.response.ProviderResponseData;
import com.flw.moka.entity.response.ProxyResponse;
import com.flw.moka.exception.NoProviderResponseDataException;
import com.flw.moka.exception.NoProviderResponseException;
import com.flw.moka.service.helper_service.ProxyResponseService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class ProviderApiUtil {

    private Environment environment;
    ProxyResponseService proxyResponseService;

    public ProxyResponse apiCallHandler(String method, ProviderPayload providerPayload, 
            ProductRequest productRequest){

		String endpoint = environment.getProperty("provider.endpoints." + method);
		URI endpointURI = URI.create(endpoint);

		ResponseEntity<ProviderResponse> responseEntity = makeProviderApiCall(
				endpointURI,
				providerPayload);
		Optional<ProviderResponse> providerResponseBody = handleNoProviderResponse(responseEntity);
		Optional<ProviderResponseData> providerResponseData = unwrapProviderResponse(providerResponseBody);

        ProxyResponse proxyResponse = proxyResponseService.createProxyResponse(providerResponseData,
                providerResponseBody,
                productRequest, method);

		return proxyResponse;
	}

    private ResponseEntity<ProviderResponse> makeProviderApiCall(URI url, ProviderPayload providerPayload) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ProviderPayload> requestEntity = new HttpEntity<>(providerPayload,
                headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<ProviderResponse> response = restTemplate.postForEntity(url,
                requestEntity, ProviderResponse.class);

        return response;
    }

    private Optional<ProviderResponse> handleNoProviderResponse(ResponseEntity<ProviderResponse> responseEntity) {
        Optional<ProviderResponse> optionalBody = Optional.ofNullable(responseEntity.getBody());

        if (optionalBody.isPresent()) {
            return optionalBody;
        } else {
            throw new NoProviderResponseException();
        }
    }

    private Optional<ProviderResponseData> unwrapProviderResponse(Optional<ProviderResponse> optionalEntity) {
        if (optionalEntity.isPresent()) {
            Optional<ProviderResponseData> optionalData = Optional.ofNullable(optionalEntity.get().getData());
            return optionalData;
        } else {
            throw new NoProviderResponseDataException();
        }
    }
}
