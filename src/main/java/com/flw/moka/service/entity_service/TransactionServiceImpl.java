package com.flw.moka.service.entity_service;

import com.flw.moka.entity.constants.Methods;
import com.flw.moka.entity.models.Transaction;
import com.flw.moka.entity.request.ProductRequest;
import com.flw.moka.entity.response.ProxyResponse;
import com.flw.moka.exception.InvalidMethodNamePassedException;
import com.flw.moka.exception.TransactionNotFoundException;
import com.flw.moka.repository.TransactionRepository;
import com.flw.moka.service.helper_service.ProxyResponseService;
import com.flw.moka.utilities.entity.RefundsUtil;
import com.flw.moka.utilities.entity.TransactionUtil;
import com.flw.moka.utilities.helpers.TimeUtil;
import com.flw.moka.validation.MethodValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@SuppressWarnings("unused")
@AllArgsConstructor
@Service
public class TransactionServiceImpl implements TransactionService {

    TransactionRepository transactionsRepository;
    ProxyResponseService proxyResponseService;
    MethodValidator methodValidator;
    TransactionUtil transactionUtill;
    RefundsUtil refundsUtil;
    LogsService logsService;

    @Override
    public void saveTransaction(ProductRequest productRequest, ProxyResponse proxyResponse,
                                Transaction transaction, String method) {
        Transaction refund = null;
        TimeUtil timeUtility = new TimeUtil();

        transaction.setResponseCode(proxyResponse.getResponseCode());
        transaction.setResponseMessage(proxyResponse.getResponseMessage());
        transaction.setProvider(proxyResponse.getMeta().getProvider());

        switch (method) {
            case Methods.AUTHORIZE:
                transactionUtill.setAuthorizeTransactionFields(transaction, productRequest, proxyResponse, timeUtility);
                break;
            case Methods.CAPTURE:
                transactionUtill.setCaptureTransactionFields(transaction, proxyResponse, timeUtility);
                break;
            case Methods.VOID:
                transactionUtill.setVoidTransactionFields(transaction, proxyResponse, timeUtility);
                break;
            case Methods.REFUND:
                refund = refundsUtil.createRefundRecord(transaction, proxyResponse, productRequest, timeUtility);
                break;
            case Methods.STATUS:
                break;
            default:
                throw new InvalidMethodNamePassedException("method not recognized.");
        }

        if (method.equalsIgnoreCase(Methods.REFUND)) {
            transactionsRepository.save(refund);
        } else {
            transactionsRepository.save(transaction);
        }
    }

    @Override
    public Transaction getTransaction(ProductRequest productRequest, String method) {
        Transaction existingTransaction;
        TimeUtil timeUtil = new TimeUtil();
        String reference = productRequest.getTransactionReference();
//        Optional<Transaction> transaction = transactionsRepository.findByTransactionReference(reference);
        Optional<Transaction> transaction = transactionsRepository.findFirstByTransactionReferenceOrderByIdDesc(reference);

        if (transaction.isEmpty()) {
            ProxyResponse proxyResponse = handleNonExistingTransaction(productRequest, timeUtil, reference, method);
            throw new TransactionNotFoundException(proxyResponse.getResponseMessage());
        } else {
            existingTransaction = transaction.get();
            if (existingTransaction.getTransactionStatus() == null) {
                ProxyResponse proxyResponse = handleNonExistingTransaction(productRequest, timeUtil, reference, method);
                throw new TransactionNotFoundException(proxyResponse.getResponseMessage());
            }
        }

        return existingTransaction;
    }

    private ProxyResponse handleNonExistingTransaction(ProductRequest productRequest, TimeUtil timeUtil, String reference, String method) {
        ProxyResponse proxyResponse = transactionUtill.prepareResponseIfTransactionDoesNotExist(reference, method);
        logsService.saveLogs(proxyResponse, productRequest, method);

        Transaction nonExistingTransaction = transactionUtill.prepareNonExistingTransaction(productRequest, timeUtil);
        saveTransaction(productRequest, proxyResponse, nonExistingTransaction, method);

        return proxyResponse;
    }


}
