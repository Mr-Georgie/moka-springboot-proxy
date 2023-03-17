package com.flw.moka.service.entities;

import com.flw.moka.entity.Transaction;

public interface TransactionService {
    Transaction saveTransaction(Transaction transactions);

    Transaction getTransaction(String ref, String method);
}
