package com.example.multitenant.repository;

import java.util.List;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import com.example.multitenant.models.Category;

import io.lettuce.core.dynamic.annotation.Param;
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
    List<Category> findAllByOrganizationIdWithChannels(@Param("organizationId") Integer organizationId);

    @Query("""
        SELECT DISTINCT c 
        FROM Category c 
        LEFT JOIN FETCH c.channels
        LEFT JOIN FETCH c.authorizedRoles
        WHERE c.organizationId = :organizationId
        ORDER BY c.displayOrder ASC
    """)
    List<Category> findAllByOrganizationIdWithChannelsAndRoles(@Param("organizationId") Integer organizationId);

    @Query("""
        SELECT DISTINCT c 
        FROM Category c 
        LEFT JOIN FETCH c.authorizedRoles
        WHERE (c.organizationId = :organizationId)
        ORDER BY c.displayOrder ASC
    """)
    List<Category> findAllByOrganizationIdWithAuthorizedRoles(@Param("organizationId") Integer organizationId);
    
    @Query("SELECT c FROM Category c WHERE c.id = :id AND c.organizationId = :organizationId")
    Category findByIdAndOrganizationId(@Param("id") Integer id, @Param("organizationId") Integer organizationId);

    @Query("""
        SELECT c FROM Category c
        LEFT JOIN FETCH c.authorizedRoles
        WHERE c.id = :id AND c.organizationId = :organizationId
    """)
    Category findByIdAndOrganizationIdWithAuthorizedRoles(@Param("id") Integer id, @Param("organizationId") Integer organizationId);
    
    @Transactional
    @Modifying
    @Query("DELETE FROM Category c WHERE c.id = :id AND c.organizationId = :organizationId")
    void deleteByIdAndOrganizationId(@Param("id") Integer id, @Param("organizationId") Integer organizationId);

    @Query("SELECT c FROM Category c WHERE c.organizationId = :organizationId ORDER BY c.displayOrder DESC")
    Category findLatestOrder(@Param("organizationId") Integer organizationId);
}