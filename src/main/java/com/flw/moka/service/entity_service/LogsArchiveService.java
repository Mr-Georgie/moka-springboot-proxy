package com.flw.moka.service.entity_service;

import java.util.List;

import com.flw.moka.entity.models.LogsArchive;

public interface LogsArchiveService {
    void saveAll(List<LogsArchive> newLogsArchives);

    List<LogsArchive> findAll();

    void deleteAll();
}
