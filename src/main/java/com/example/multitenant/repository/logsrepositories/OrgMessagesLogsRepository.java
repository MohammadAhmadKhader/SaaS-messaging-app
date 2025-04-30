package com.example.multitenant.repository.logsrepositories;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.example.multitenant.models.logsmodels.OrgMessageLog;
import com.example.multitenant.repository.GenericRepository;

@Repository
public interface OrgMessagesLogsRepository extends GenericRepository<OrgMessageLog ,UUID>{
    
}