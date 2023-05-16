package com.flw.moka.service.helper_service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.flw.moka.entity.models.Logs;
import com.flw.moka.entity.models.LogsArchive;
import com.flw.moka.service.entity_service.LogsArchiveService;
import com.flw.moka.service.entity_service.LogsService;

import lombok.AllArgsConstructor;

@SuppressWarnings("unused")
@AllArgsConstructor
@Service
public class ArchiveRecordsService {

    private LogsService logsService;
    private LogsArchiveService logsArchiveService;

    @SuppressWarnings("unused")
    @Scheduled(cron = "0 0 0 31 12 ?") // runs every 31st December at midnight (00:00:00)
    public void init() {
        archiveLogs();
    }

    public void archiveLogs() {
        List<Logs> logsList = logsService.findAll();
        List<LogsArchive> newLogsArchives = new ArrayList<>();

        for (Logs log : logsList) {
            LogsArchive archive = new LogsArchive();
            archive.setTransactionReference(log.getTransactionReference());
            archive.setExternalReference(log.getExternalReference());
            archive.setMethod(log.getMethod());
            archive.setBody(log.getBody());
            archive.setResponse(log.getResponse());
            archive.setTimeIn(log.getTimeIn());
            newLogsArchives.add(archive);
        }

        logsArchiveService.saveAll(newLogsArchives);
        logsService.deleteAll();

        System.out.println("==================================");
        System.out.println("Logs archived successfully");
        System.out.println("==================================");
    }

    @SuppressWarnings("unused")
    public void unArchiveLogs() {
        List<Logs> logsList = new ArrayList<>();
        List<LogsArchive> logsArchives = logsArchiveService.findAll();

        for (LogsArchive log : logsArchives) {
            Logs newLog = new Logs();
            newLog.setTransactionReference(log.getTransactionReference());
            newLog.setExternalReference(log.getExternalReference());
            newLog.setMethod(log.getMethod());
            newLog.setBody(log.getBody());
            newLog.setResponse(log.getResponse());
            newLog.setTimeIn(log.getTimeIn());
            logsList.add(newLog);
        }

        logsService.saveAll(logsList);
        logsArchiveService.deleteAll();
    }

}
