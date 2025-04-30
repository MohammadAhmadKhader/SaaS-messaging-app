package com.example.multitenant.repository.logsrepositories;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.example.multitenant.models.logsmodels.KicksLog;
import com.example.multitenant.repository.GenericRepository;

@Repository
public interface KickLogsRepository extends GenericRepository<KicksLog, UUID> {
    
}
