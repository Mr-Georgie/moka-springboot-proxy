package com.flw.moka.service.entity_service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.flw.moka.entity.models.Transaction;
import com.flw.moka.repository.TransactionRepository;
import com.flw.moka.service.helper_service.ProxyResponseService;
import com.flw.moka.validation.MethodValidator;

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
    public Optional<Transaction> getTransaction(String ref) {

        Optional<Transaction> transactions = transactionsRepository.findByTransactionReference(ref);
        return transactions;
    }
}
