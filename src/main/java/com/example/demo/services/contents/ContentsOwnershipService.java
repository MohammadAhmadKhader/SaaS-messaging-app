package com.example.demo.services.contents;

import org.springframework.stereotype.Service;

import com.example.demo.models.Content;
import com.example.demo.repository.ContentsRepository;
import com.example.demo.services.cache.RedisService;
import com.example.demo.services.ownership.impl.OwnershipServiceImpl;

@Service
public class ContentsOwnershipService extends OwnershipServiceImpl<Content, Integer, ContentsRepository> {
    
    public ContentsOwnershipService(ContentsRepository contentsRepository, RedisService redisService) {
        super(contentsRepository, redisService, () -> {
            return new Content();
        });
    }
}