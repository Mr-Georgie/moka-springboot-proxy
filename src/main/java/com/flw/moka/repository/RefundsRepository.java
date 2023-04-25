package com.flw.moka.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.flw.moka.entity.models.Refunds;

public interface RefundsRepository extends CrudRepository<Refunds, Long> {
    Optional<Refunds> findByRefundReference(String refundReference);

    Optional<Refunds> findByTransactionReference(String transactionReference);

    // Optional<Refunds> findFirstByOrderByTransactionReferenceAsc();

    Optional<Refunds> findFirstByTransactionReferenceOrderByIdDesc(String transactionReference);
}
