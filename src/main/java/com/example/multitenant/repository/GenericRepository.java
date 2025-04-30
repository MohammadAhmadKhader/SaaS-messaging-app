package com.example.multitenant.repository;

import java.util.function.Consumer;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * this can be used in case we want to force a common logic upon all repositories
 */
@NoRepositoryBean
public interface GenericRepository<TModel, ID> extends JpaRepository<TModel, ID>, JpaSpecificationExecutor<TModel> {
    
}