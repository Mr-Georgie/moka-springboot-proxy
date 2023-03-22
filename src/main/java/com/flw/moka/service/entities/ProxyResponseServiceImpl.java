package com.flw.moka.service.entities;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.flw.moka.entity.CardParams;
import com.flw.moka.entity.helpers.ProductRequest;
import com.flw.moka.entity.helpers.ProviderResponse;
import com.flw.moka.entity.helpers.ProviderResponseData;
import com.flw.moka.entity.helpers.ProxyResponse;
import com.flw.moka.repository.CardParamsRepository;
import com.flw.moka.repository.ProxyResponseRepository;
import com.flw.moka.utilities.TimeUtil;
import com.google.gson.Gson;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class ProxyResponseServiceImpl implements ProxyResponseService {

    ProxyResponseRepository proxyResponseRepository;
    CardParamsRepository cardParamsRepository;

    @Override
    public ProxyResponse createProxyResponse(Optional<ProviderResponseData> dataEntity,
            Optional<ProviderResponse> bodyEntity, ProductRequest productRequest) {
        return proxyResponseRepository.setProxyResponse(dataEntity, bodyEntity, productRequest);
    }

    public void saveFailedResponseToDB(ProxyResponse proxyResponse,
            String transactionRef, String method) {
        CardParams cardParams = new CardParams();
        Gson json = new Gson();
        TimeUtil timeUtility = new TimeUtil();

        cardParams.setMethod(method);
        cardParams.setTimeIn(timeUtility.getDateTime());
        cardParams.setResponse(json.toJson(proxyResponse));
        cardParams.setTransactionRef(transactionRef);
        cardParams.setBody("N/A");
        cardParams.setExternalRef("N/A");

        cardParamsRepository.save(cardParams);

    }
}
