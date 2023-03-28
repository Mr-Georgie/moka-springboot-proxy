package com.flw.moka.service.entity_service;

import java.util.List;

import com.flw.moka.entity.models.Logs;

public interface LogsService {

    Logs saveLogs(Logs logs);

    void saveAll(List<Logs> logsList);

    List<Logs> findAll();

    void deleteAll();

}
