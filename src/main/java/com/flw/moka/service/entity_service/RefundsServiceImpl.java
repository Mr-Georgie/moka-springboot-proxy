package com.flw.moka.service.entity_service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.flw.moka.entity.models.Refunds;
import com.flw.moka.repository.RefundsRepository;
import com.flw.moka.service.helper_service.ProxyResponseService;
import com.flw.moka.validation.MethodValidator;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class RefundsServiceImpl implements RefundsService {

    RefundsRepository refundsRepository;
    ProxyResponseService proxyResponseService;
    MethodValidator methodValidator;

    @Override
    public Refunds saveRefund(Refunds refund) {
        return refundsRepository.save(refund);
    }

    @Override
    public Optional<Refunds> getRefund(String ref) {
        Optional<Refunds> refund = refundsRepository.findByTransactionReference(ref);
        return refund;
    }
}
