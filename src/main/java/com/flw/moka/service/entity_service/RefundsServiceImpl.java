package com.flw.moka.service.entity_service;

import org.springframework.stereotype.Service;

import com.flw.moka.entity.Refunds;
import com.flw.moka.repository.entity_repos.RefundsRepository;
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

    // @Override
    // public Refunds getRefund(String ref, String method) {
    //     Optional<Refunds> refund = refundsRepository.findByTransactionRef(ref);
    //     Refunds existingTransaction = unwrapTransactions(refund, ref, method);
    //     return methodValidator.preventDuplicateMethodCall(existingTransaction, ref, method);
    // }
}
