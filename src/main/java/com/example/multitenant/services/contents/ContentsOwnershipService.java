package com.example.multitenant.services.contents;

import org.springframework.stereotype.Service;

import com.example.multitenant.models.Content;
import com.example.multitenant.repository.ContentsRepository;
import com.example.multitenant.services.cache.SessionsService;
import com.example.multitenant.services.ownership.impl.OwnershipServiceImpl;

@Service
public class ContentsOwnershipService extends OwnershipServiceImpl<Content, Integer, ContentsRepository> {
    
    public ContentsOwnershipService(ContentsRepository contentsRepository, SessionsService sessionsService) {
        super(contentsRepository, sessionsService, () -> {
            return new Content();
        });
    }
}