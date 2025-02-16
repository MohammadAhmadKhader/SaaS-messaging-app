package com.example.demo.services.ownership;

import org.springframework.stereotype.Service;

import com.example.demo.models.Content;
import com.example.demo.repository.contents.ContentsRepository;
import com.example.demo.services.cache.RedisService;
import com.example.demo.services.ownership.impl.UserOwnershipServiceImpl;

@Service
public class ContentsOwnershipService extends UserOwnershipServiceImpl<Content, Integer, ContentsRepository> {
    
    public ContentsOwnershipService(ContentsRepository contentsRepository, RedisService redisService) {
        super(contentsRepository, redisService, Content.class);
    }
}