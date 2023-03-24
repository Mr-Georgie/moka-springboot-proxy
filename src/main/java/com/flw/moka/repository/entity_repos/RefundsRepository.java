package com.flw.moka.repository.entity_repos;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.flw.moka.entity.Refunds;

public interface RefundsRepository extends CrudRepository<Refunds, Long> {
    Optional<Refunds> findByTransactionRef(String transactionRef);
}