package com.example.multitenant.repository;

import java.util.List;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import com.example.multitenant.models.Category;

import io.lettuce.core.dynamic.annotation.Param;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;

@Repository
public interface CategoriesRepository extends GenericRepository<Category, Integer> {
    @Query("""
        SELECT DISTINCT c 
        FROM Category c 
        LEFT JOIN FETCH c.channels 
        WHERE c.organizationId = :organizationId
        ORDER BY c.displayOrder ASC
    """)
    List<Category> findAllByOrgIdWithChannels(@Param("organizationId") Integer organizationId);

    @Query("""
        SELECT DISTINCT c 
        FROM Category c 
        LEFT JOIN FETCH c.channels
        LEFT JOIN FETCH c.authorizedRoles
        WHERE c.organizationId = :organizationId
        ORDER BY c.displayOrder ASC
    """)
    List<Category> findAllByOrgIdWithChannelsAndRoles(@Param("organizationId") Integer organizationId);

    @Query("""
        SELECT DISTINCT c 
        FROM Category c 
        LEFT JOIN FETCH c.authorizedRoles
        WHERE (c.organizationId = :organizationId)
        ORDER BY c.displayOrder ASC
    """)
    List<Category> findAllByOrgIdWithAuthorizedRoles(@Param("organizationId") Integer organizationId);
    
    @Query("SELECT c FROM Category c WHERE c.id = :id AND c.organizationId = :organizationId")
    Category findByIdAndOrgId(@Param("id") Integer id, @Param("organizationId") Integer organizationId);

    @Query("""
        SELECT c FROM Category c
        LEFT JOIN FETCH c.authorizedRoles
        WHERE c.id = :id AND c.organizationId = :organizationId
    """)
    Category findByIdAndOrgIdWithAuthorizedRoles(@Param("id") Integer id, @Param("organizationId") Integer organizationId);
    
    @Transactional
    @Modifying
    @Query("DELETE FROM Category c WHERE c.id = :id AND c.organizationId = :organizationId")
    void deleteByIdAndOrgId(@Param("id") Integer id, @Param("organizationId") Integer organizationId);

    @Query("SELECT c FROM Category c WHERE c.organizationId = :organizationId ORDER BY c.displayOrder DESC LIMIT 1")
    Category findLatestOrder(@Param("organizationId") Integer organizationId);

    @Query("SELECT COUNT(c) FROM Category c WHERE c.organizationId = :organizationId")
    long countCategoriesByOrgId(@Param("organizationId") long organizationId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Category c WHERE c.id = :id AND c.organizationId = :organizationId")
    Category findByIdAndOrgIdLocked(@Param("id") Integer id, @Param("organizationId") Integer organizationId);
}