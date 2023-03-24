package com.flw.moka.service.helper_service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.flw.moka.entity.CardParams;
import com.flw.moka.entity.helpers.Methods;
import com.flw.moka.entity.helpers.ProductRequest;
import com.flw.moka.entity.helpers.ProviderResponse;
import com.flw.moka.entity.helpers.ProviderResponseData;
import com.flw.moka.entity.helpers.ProxyResponse;
import com.flw.moka.exception.TransactionMethodAlreadyDoneException;
import com.flw.moka.repository.entity_repos.CardParamsRepository;
import com.flw.moka.repository.helper_repos.ProxyResponseRepository;
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
            Optional<ProviderResponse> bodyEntity, ProductRequest productRequest, String method) {
        return proxyResponseRepository.setProxyResponse(dataEntity, bodyEntity, productRequest, method);
    }

    @Override
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

    @Override
    public <T> T sendMethodAlreadyDoneResponse(String transactionCurrentStatus, String transactionRef,
            Class<T> returnType) {

        if (transactionCurrentStatus.equalsIgnoreCase(Methods.CAPTURE)) {
            String statusInPastTense = transactionCurrentStatus + "d";
            throw new TransactionMethodAlreadyDoneException(transactionRef, statusInPastTense);
        }

        if (transactionCurrentStatus.equalsIgnoreCase(Methods.VOID)) {
            String statusInPastTense = transactionCurrentStatus + "ed";
            throw new TransactionMethodAlreadyDoneException(transactionRef, statusInPastTense);
        }

        if (transactionCurrentStatus.equalsIgnoreCase(Methods.REFUND)) {
            String statusInPastTense = transactionCurrentStatus + "ed";
            throw new TransactionMethodAlreadyDoneException(transactionRef, statusInPastTense);
        }

        // If none of the above conditions match, throw an IllegalArgumentException
        throw new IllegalArgumentException("Invalid status: " + transactionCurrentStatus);
    }

}
