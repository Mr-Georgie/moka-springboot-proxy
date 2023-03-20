package com.flw.moka.service.entities;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.flw.moka.entity.Transaction;
import com.flw.moka.entity.helpers.Methods;
import com.flw.moka.entity.helpers.ProxyResponse;
import com.flw.moka.exception.TransactionAlreadyCapturedException;
import com.flw.moka.exception.TransactionAlreadyRefundedException;
import com.flw.moka.exception.TransactionAlreadyVoidedException;
import com.flw.moka.exception.TransactionNotFoundException;
import com.flw.moka.repository.TransactionRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class TransactionServiceImpl implements TransactionService {

    TransactionRepository transactionsRepository;
    ProxyResponseService proxyResponseService;

    @Override
    public Transaction saveTransaction(Transaction transactions) {
        return transactionsRepository.save(transactions);
    }

    @Override
    public Transaction getTransaction(String ref, String method) {
        Optional<Transaction> transactions = transactionsRepository.findByTransactionRef(ref);
        return unwrapTransactions(transactions, ref, method);
    }

    private Transaction unwrapTransactions(Optional<Transaction> entity, String ref, String method) {
        ProxyResponse proxyResponse = new ProxyResponse();

        if (entity.isPresent()) {

            Transaction existingTransaction = entity.get();

            return checkTransactionStatus(existingTransaction, ref, method);

        } else {
            proxyResponse.setMessage("The transaction reference does not exist");
            proxyResponse.setCode("RR-400");
            proxyResponse.setProvider("MOKA");

            proxyResponseService.saveFailedResponseToDB(proxyResponse, ref, method);
            throw new TransactionNotFoundException(ref);
        }
    }

    private Transaction checkTransactionStatus(
            Transaction entity, String transactionRef, String method) {

        ProxyResponse proxyResponse = new ProxyResponse();

        // the first condition before the || operator is to catch already 
        // captured transaction that their status is now void
        
        if (Methods.CAPTURE.equalsIgnoreCase(method) && entity.getTimeCaptured() != null
                || entity.getTransactionStatus().equalsIgnoreCase(method)) {
            proxyResponse.setMessage("This transaction status is: " + method.toUpperCase());
            proxyResponse.setCode("RR-400");
            proxyResponse.setProvider("MOKA");

            proxyResponseService.saveFailedResponseToDB(proxyResponse, transactionRef, method);

            return sendCorrectErrorResponse(method, transactionRef);
        }

        return entity;

    }

    private Transaction sendCorrectErrorResponse(String method, String transactionRef) {

        if (method.equalsIgnoreCase(Methods.CAPTURE)) {
            throw new TransactionAlreadyCapturedException(transactionRef);
        }

        if (method.equalsIgnoreCase(Methods.VOID)) {
            throw new TransactionAlreadyVoidedException(transactionRef);
        }

        if (method.equalsIgnoreCase(Methods.REFUND)) {
            throw new TransactionAlreadyRefundedException(transactionRef);
        }

        // If none of the above conditions match, throw an IllegalArgumentException
        throw new IllegalArgumentException("Invalid method: " + method);
    }
}
