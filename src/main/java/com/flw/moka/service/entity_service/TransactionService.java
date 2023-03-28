package com.flw.moka.service.entity_service;

import java.util.Optional;

import com.flw.moka.entity.models.Transaction;

public interface TransactionService {
    Transaction saveTransaction(Transaction transactions);

    Optional<Transaction> getTransaction(String ref);
}
