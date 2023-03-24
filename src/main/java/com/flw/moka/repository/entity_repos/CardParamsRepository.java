package com.flw.moka.repository.entity_repos;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.flw.moka.entity.CardParams;

public interface CardParamsRepository extends CrudRepository<CardParams, Long> {
    List<CardParams> findAllByTransactionRef(String transactionRef);
}