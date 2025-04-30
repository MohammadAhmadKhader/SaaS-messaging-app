package com.example.multitenant.repository.logsrepositories;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.example.multitenant.models.logsmodels.AuthLog;
import com.example.multitenant.repository.GenericRepository;

@Repository
public interface AuthLogsRepository extends GenericRepository<AuthLog, UUID> {
    
}
