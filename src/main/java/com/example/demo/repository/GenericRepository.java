package com.example.demo.repository;

import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * this can be used in case we want to force a common logic upon all repositories
 */
@Primary
@NoRepositoryBean
public interface GenericRepository<T, ID> extends JpaRepository<T, ID> {
    
}