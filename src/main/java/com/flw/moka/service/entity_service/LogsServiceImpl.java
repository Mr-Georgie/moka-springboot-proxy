package com.flw.moka.service.entity_service;

import org.springframework.stereotype.Service;

import com.flw.moka.entity.models.Logs;
import com.flw.moka.repository.LogsRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class LogsServiceImpl implements LogsService {

    LogsRepository logsRepository;

    @Override
    public Logs saveLogs(Logs logs) {
        return logsRepository.save(logs);
    }
}
