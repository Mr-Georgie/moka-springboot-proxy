package com.flw.moka.service.entity_service;

import java.util.Optional;

import com.flw.moka.entity.models.Refunds;

public interface RefundsService {
    Refunds saveRefund(Refunds refund);

    Optional<Refunds> getRefund(String ref);
}
