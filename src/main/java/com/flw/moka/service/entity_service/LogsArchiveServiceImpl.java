package com.flw.moka.service.entity_service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.flw.moka.entity.models.LogsArchive;
import com.flw.moka.repository.LogsArchiveRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class LogsArchiveServiceImpl implements LogsArchiveService {

    LogsArchiveRepository logsArchiveRepository;

    @Override
    public void saveAll(List<LogsArchive> newLogsArchives) {
        logsArchiveRepository.saveAll(newLogsArchives);
    }

    @Override
    public List<LogsArchive> findAll() {
        return logsArchiveRepository.findAll();
    }

    @Override
    public void deleteAll() {
        logsArchiveRepository.deleteAll();
    }
}
