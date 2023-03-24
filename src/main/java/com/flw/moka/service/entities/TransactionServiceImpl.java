package com.flw.moka.service.entities;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.flw.moka.entity.Transaction;
import com.flw.moka.entity.helpers.ProxyResponse;
import com.flw.moka.exception.TransactionNotFoundException;
import com.flw.moka.repository.TransactionRepository;
import com.flw.moka.utilities.MethodValidator;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class TransactionServiceImpl implements TransactionService {

    TransactionRepository transactionsRepository;
    ProxyResponseService proxyResponseService;
    MethodValidator methodValidator;

    @Override
    public Transaction saveTransaction(Transaction transactions) {
        return transactionsRepository.save(transactions);
    }

    @Override
    public Transaction getTransaction(String ref, String method) {
        Optional<Transaction> transactions = transactionsRepository.findByTransactionRef(ref);
        Transaction existingTransaction = unwrapTransactions(transactions, ref, method);
        return methodValidator.preventDuplicateMethodCall(existingTransaction, ref, method);
    }

    private Transaction unwrapTransactions(Optional<Transaction> entity, String ref, String method) {
        ProxyResponse proxyResponse = new ProxyResponse();

        if (entity.isPresent()) {

            Transaction existingTransaction = entity.get();

            return existingTransaction;

        } else {
            proxyResponse.setMessage("The transaction reference does not exist");
            proxyResponse.setCode("RR-400");
            proxyResponse.setProvider("MOKA");

            proxyResponseService.saveFailedResponseToDB(proxyResponse, ref, method);
            throw new TransactionNotFoundException(ref);
        }
    }
}
