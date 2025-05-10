package com.example.multitenant.repository;

import java.util.List;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import com.example.multitenant.models.Channel;

import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;

@Repository
public interface ChannelsRepository extends GenericRepository<Channel, Integer> {
    @Query("SELECT c FROM Channel c WHERE c.organization.id = :organizationId  ORDER BY c.displayOrder ASC")
    List<Channel> findAllByOrgId(@Param("organizationId") Integer organizationId);

    @Query("SELECT c FROM Channel c WHERE c.id = :id AND c.organization.id = :organizationId")
    Channel findByIdAndOrgId(@Param("id") Integer id, @Param("organizationId") Integer organizationId);

    @Query("""
        SELECT c FROM Channel c
        LEFT JOIN FETCH c.messages m
        WHERE c.id = :id AND c.organization.id = :organizationId
    """)
    Channel findWithMessagesByIdAndOrgId(@Param("id") Integer id, @Param("organizationId") Integer organizationId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Channel c WHERE (c.id = :id AND c.organization.id = :organizationId AND c.categoryId = :categoryId)")
    void deleteByIdAndOrgId(@Param("id") Integer id, @Param("organizationId") Integer organizationId, @Param("categoryId") Integer categoryId);

    @Query("SELECT c FROM Channel c WHERE (c.organizationId = :organizationId AND c.categoryId = :categoryId) ORDER BY c.displayOrder DESC")
    Channel findLatestOrder(@Param("organizationId") Integer organizationId, @Param("categoryId") Integer categoryId);

    @Query("SELECT COUNT(c) FROM Channel c WHERE c.organizationId = :organizationId")
    long countChannelsByOrgId(@Param("organizationId") Integer organizationId);

    @Query("SELECT COUNT(c) FROM Channel c WHERE c.category.id = :categoryId AND c.organizationId = :organizationId")
    long countChannelsByCategoryId(@Param("categoryId") Integer categoryId, @Param("organizationId") Integer organizationId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Channel c WHERE c.id = :id AND c.organizationId = :organizationId AND c.category.id = :categoryId")
    Channel findByIdAndOrgIdAndCatIdLocked(@Param("id") Integer id, @Param("organizationId") Integer organizationId, @Param("categoryId") Integer categoryId);
}