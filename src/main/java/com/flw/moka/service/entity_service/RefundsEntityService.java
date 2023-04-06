package com.flw.moka.service.entity_service;

import java.util.Optional;

import com.flw.moka.entity.models.Refunds;

public interface RefundsEntityService {
    Refunds saveRefund(Refunds refund);

    Optional<Refunds> getRefundByRefundReference(String ref);

    Optional<Refunds> getRefundByTransactionReference(String reference);
}
