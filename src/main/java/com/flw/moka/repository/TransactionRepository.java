package com.flw.moka.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.flw.moka.entity.models.Transaction;

public interface TransactionRepository extends CrudRepository<Transaction, Long> {
    Optional<Transaction> findByTransactionReference(String transactionReference);
}
