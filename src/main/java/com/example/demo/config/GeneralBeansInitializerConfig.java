package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.example.demo.models.Content;
import com.example.demo.models.Organization;
import com.example.demo.repository.contents.ContentsRepository;
import com.example.demo.repository.generic.GenericRepository;
import com.example.demo.repository.organizations.OrganizationsRepository;
import com.example.demo.services.cache.RedisService;
import com.example.demo.services.ownership.contract.OrganizationOwnershipEntity;
import com.example.demo.services.ownership.impl.OrganizationOwnershipServiceImpl;

/**
 * This config is being used to solve the issue of not identified beans when classes are passed through the constructor:
 * 
 * Example:
 * {@link src/main/com/example/demo/services/ownership/ContentsOwnershipService.java}
 * 
 * Some classes requires at least 1 bean, therefore they were initialized here.
 */
@Configuration
public class GeneralBeansInitializerConfig {
    
    @Bean
    Class<Content> contentClass() {
        return Content.class;
    }

    @Bean
    Class<Organization> organizationClass() {
        return Organization.class;
    }

    @Bean
    OrganizationOwnershipServiceImpl<Content, Integer, ContentsRepository> contentsOrganizationOwnershipServiceImpl(ContentsRepository contentRepo, RedisService redis) {
        return new OrganizationOwnershipServiceImpl<Content,Integer,ContentsRepository>(contentRepo, redis, Content.class);
    }  
}
