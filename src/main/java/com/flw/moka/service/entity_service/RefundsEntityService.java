package com.flw.moka.service.entity_service;

import java.util.Optional;

import com.flw.moka.entity.models.Refunds;
import com.flw.moka.entity.models.Transaction;
import com.flw.moka.entity.request.ProductRequest;
import com.flw.moka.entity.response.ProxyResponse;

public interface RefundsEntityService {
    
    void saveRefund(ProxyResponse proxyResponse, Refunds existingRefund, Transaction transaction);

    Refunds getRefund(ProductRequest productRequest, Transaction transaction);

    Optional<Refunds> getRefundByTransactionReference(String reference);

    Optional<Refunds> findLastTransactionOccurrence(String reference);
}
