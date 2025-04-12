package com.example.multitenant.repository;

import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.stereotype.Repository;

import com.example.multitenant.models.EmptyEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/**
 * This and the EmptyEntity were made to solve an issue with the GenericService not being able to select a candidate 
 * {@see GenericService}
 */
@Primary
@Repository
public interface UnImplementedRepository extends JpaRepository<EmptyEntity, Integer> {
    
}