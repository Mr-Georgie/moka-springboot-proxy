package com.flw.moka.repository.entity_repos;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.flw.moka.entity.Transaction;

public interface TransactionRepository extends CrudRepository<Transaction, Long> {
    Optional<Transaction> findByTransactionRef(String transactionRef);
}
