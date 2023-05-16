package com.flw.moka.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.flw.moka.entity.models.LogsArchive;

@SuppressWarnings("ALL")
public interface LogsArchiveRepository extends CrudRepository<LogsArchive, Long> {
    List<LogsArchive> findAll();
}
