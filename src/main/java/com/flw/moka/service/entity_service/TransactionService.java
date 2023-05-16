package com.flw.moka.service.entity_service;

import com.flw.moka.entity.models.Transaction;
import com.flw.moka.entity.request.ProductRequest;
import com.flw.moka.entity.response.ProxyResponse;

public interface TransactionService {
    void saveTransaction(ProductRequest productRequest, ProxyResponse proxyResponse,
                         Transaction transaction, String method);

    Transaction getTransaction(ProductRequest productRequest, String method);

}
