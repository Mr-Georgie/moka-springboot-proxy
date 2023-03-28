package com.flw.moka.utilities.helpers;

import java.net.URI;
import java.util.Optional;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.flw.moka.entity.request.ProviderPayload;
import com.flw.moka.entity.response.ProviderResponse;
import com.flw.moka.entity.response.ProviderResponseData;
import com.flw.moka.exception.NoProviderResponseDataException;
import com.flw.moka.exception.NoProviderResponseException;

@Component
public class ProviderApiUtil {

    public ResponseEntity<ProviderResponse> makeProviderApiCall(URI url, ProviderPayload providerPayload) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ProviderPayload> requestEntity = new HttpEntity<>(providerPayload,
                headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<ProviderResponse> response = restTemplate.postForEntity(url,
                requestEntity, ProviderResponse.class);

        return response;
    }

    public Optional<ProviderResponse> handleNoProviderResponse(ResponseEntity<ProviderResponse> responseEntity) {
        Optional<ProviderResponse> optionalBody = Optional.ofNullable(responseEntity.getBody());

        if (optionalBody.isPresent()) {
            return optionalBody;
        } else {
            throw new NoProviderResponseException();
        }
    }

    public Optional<ProviderResponseData> unwrapProviderResponse(Optional<ProviderResponse> optionalEntity) {
        if (optionalEntity.isPresent()) {
            Optional<ProviderResponseData> optionalData = Optional.ofNullable(optionalEntity.get().getData());
            return optionalData;
        } else {
            throw new NoProviderResponseDataException();
        }
    }
}
