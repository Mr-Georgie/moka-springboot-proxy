package com.flw.moka.utilities;

import java.net.URI;
import java.util.Optional;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.flw.moka.entity.helpers.ProviderPayload;
import com.flw.moka.entity.helpers.ProviderResponse;
import com.flw.moka.entity.helpers.ProviderResponseData;
import com.flw.moka.exception.NoProviderResponseDataException;
import com.flw.moka.exception.NoProviderResponseException;

@Component
public class ProviderApiUtility {

    public ResponseEntity<ProviderResponse> makeApiCall(URI url, ProviderPayload providerPayload) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ProviderPayload> requestEntity = new HttpEntity<>(providerPayload,
                headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<ProviderResponse> response = restTemplate.postForEntity(url,
                requestEntity, ProviderResponse.class);

        return response;
    }

    public Optional<ProviderResponse> handleNoResponse(ResponseEntity<ProviderResponse> responseEntity) {
        Optional<ProviderResponse> optionalBody = Optional.ofNullable(responseEntity.getBody());

        if (optionalBody.isPresent()) {
            return optionalBody;
        } else {
            throw new NoProviderResponseException();
        }
    }

    public Optional<ProviderResponseData> unwrapResponse(Optional<ProviderResponse> optionalEntity) {
        if (optionalEntity.isPresent()) {
            Optional<ProviderResponseData> optionalData = Optional.ofNullable(optionalEntity.get().getData());
            return optionalData;
        } else {
            throw new NoProviderResponseDataException();
        }
    }
}
