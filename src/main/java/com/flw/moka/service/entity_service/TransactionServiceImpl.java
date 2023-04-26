package com.flw.moka.service.entity_service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.flw.moka.entity.constants.Methods;
import com.flw.moka.entity.models.Refunds;
import com.flw.moka.entity.models.Transaction;
import com.flw.moka.entity.request.ProductRequest;
import com.flw.moka.entity.response.ProxyResponse;
import com.flw.moka.exception.InvalidMethodNamePassedException;
import com.flw.moka.exception.TransactionNotFoundException;
import com.flw.moka.repository.TransactionRepository;
import com.flw.moka.service.helper_service.ProxyResponseService;
import com.flw.moka.utilities.helpers.LogsUtil;
import com.flw.moka.utilities.helpers.RefundsUtil;
import com.flw.moka.utilities.helpers.TimeUtil;
import com.flw.moka.utilities.helpers.TransactionUtil;
import com.flw.moka.validation.MethodValidator;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class TransactionServiceImpl implements TransactionService {

    TransactionRepository transactionsRepository;
    ProxyResponseService proxyResponseService;
    MethodValidator methodValidator;
    LogsUtil logsUtil;
    RefundsUtil refundsUtil;
    TransactionUtil transactionUtill;

    @Override
    public void saveTransaction(ProductRequest productRequest, ProxyResponse proxyResponse,
            Transaction transaction, String method) {

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
                transactionUtill.setRefundTransactionFields(transaction, proxyResponse, timeUtility);
                break;
            case Methods.STATUS:
                break;
            default:
                throw new InvalidMethodNamePassedException("method not recognized.");
        }

        saveTransactionToDatabase(transaction);
    }

    @Override
    public Transaction getTransaction(ProductRequest productRequest, String method) {
        String reference = productRequest.getTransactionReference();
        TimeUtil timeUtility = new TimeUtil();
        Optional<Transaction> transaction = getTransactionIfExistInDB(reference);

        if (transaction.isEmpty()) {

            ProxyResponse proxyResponse = transactionUtill.prepareResponseIfTransactionDoesNotExist(reference, method);
            logsUtil.setLogs(proxyResponse, productRequest, method);

            Transaction nonExistingTransaction = transactionUtill.prepareNonExistingTransaction(productRequest, timeUtility);
            if (method.equalsIgnoreCase(Methods.REFUND)) {
                Refunds newRefund = new Refunds();
                refundsUtil.saveRefundToDataBase(proxyResponse, newRefund, nonExistingTransaction);
            } else {
                saveTransaction(productRequest, proxyResponse, nonExistingTransaction, method);
            }
            throw new TransactionNotFoundException(proxyResponse.getResponseMessage());
        }

        Transaction existingTransaction = transaction.get();
        if (existingTransaction.getTransactionStatus() == null) {
            ProxyResponse proxyResponse = transactionUtill.prepareResponseIfTransactionDoesNotExist(reference, method);
            logsUtil.setLogs(proxyResponse, productRequest, method);

            saveTransaction(productRequest, proxyResponse, existingTransaction, method);
            throw new TransactionNotFoundException(proxyResponse.getResponseMessage());
        }

        return existingTransaction;
    }

    @Override
    public Optional<Transaction> getTransactionIfExistInDB(String ref) {

        Optional<Transaction> transactions = transactionsRepository.findByTransactionReference(ref);
        return transactions;
    }

    private Transaction saveTransactionToDatabase(Transaction transactions) {
        return transactionsRepository.save(transactions);
    }
}

