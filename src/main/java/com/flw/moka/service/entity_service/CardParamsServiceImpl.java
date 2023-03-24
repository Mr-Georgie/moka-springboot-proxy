package com.flw.moka.service.entity_service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.flw.moka.entity.CardParams;
import com.flw.moka.entity.helpers.ProxyResponse;
import com.flw.moka.exception.TransactionAlreadyCapturedException;
import com.flw.moka.exception.TransactionNotFoundException;
import com.flw.moka.repository.entity_repos.CardParamsRepository;
import com.flw.moka.service.helper_service.ProxyResponseService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class CardParamsServiceImpl implements CardParamsService {

    CardParamsRepository cardParamsRepository;
    ProxyResponseService proxyResponseService;

    @Override
    public CardParams getCardParams(String transactionRef, String method) {
        List<CardParams> cardParams = cardParamsRepository.findAllByTransactionRef(transactionRef);
        return unwrapCardParams(cardParams, transactionRef, method);
    }

    @Override
    public CardParams saveCardParams(CardParams cardParams) {
        return cardParamsRepository.save(cardParams);
    }

    private CardParams unwrapCardParams(List<CardParams> entity, String transactionRef, String method) {

        List<CardParams> existingEntity = checkIfCardParamsExist(entity, transactionRef, method);

        CardParams cardNotCaptured = checkIfCardIsCaptured(existingEntity, transactionRef, method);

        return cardNotCaptured;
    }

    private List<CardParams> checkIfCardParamsExist(List<CardParams> entity, String transactionRef, String method) {
        ProxyResponse proxyResponse = new ProxyResponse();

        if (entity.isEmpty()) {
            proxyResponse.setMessage("The transaction reference does not exist");
            proxyResponse.setCode("RR-400");
            proxyResponse.setProvider("MOKA");

            proxyResponseService.saveFailedResponseToDB(proxyResponse, transactionRef, method);
            throw new TransactionNotFoundException(transactionRef);
        } else {
            return entity;
        }
    }

    private CardParams checkIfCardIsCaptured(List<CardParams> entity, String transactionRef, String method) {
        CardParams existingEntity = entity.get(entity.size() - 1);
        ProxyResponse proxyResponse = new ProxyResponse();

        if (existingEntity.getMethod() == method) {
            proxyResponse
                    .setMessage("The method" + method.toUpperCase().toString() + "has been done on this transaction");
            proxyResponse.setCode("RR-400");
            proxyResponse.setProvider("MOKA");

            proxyResponseService.saveFailedResponseToDB(proxyResponse, transactionRef, method);

            throw new TransactionAlreadyCapturedException(transactionRef);
        } else {
            return existingEntity;
        }
    }

}
