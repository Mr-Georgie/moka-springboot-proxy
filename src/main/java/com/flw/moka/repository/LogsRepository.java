package com.flw.moka.repository;

import org.springframework.data.repository.CrudRepository;

import com.flw.moka.entity.models.Logs;

public interface LogsRepository extends CrudRepository<Logs, Long> {

}
