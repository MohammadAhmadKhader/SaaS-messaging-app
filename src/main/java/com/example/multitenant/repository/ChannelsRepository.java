package com.example.multitenant.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.multitenant.models.Channel;

import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;

@Repository
public interface ChannelsRepository extends GenericRepository<Channel, Integer> {
    @Query("SELECT c FROM Channel c WHERE c.organization.id = :organizationId")
    List<Channel> findAllByOrganizationId(@Param("organizationId") Integer organizationId);

    @Query("SELECT c FROM Channel c WHERE c.id = :id AND c.organization.id = :organizationId")
    Channel findByIdAndOrganizationId(@Param("id") Integer id, @Param("organizationId") Integer organizationId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Channel c WHERE c.id = :id AND c.organization.id = :organizationId")
    void deleteByIdAndOrganizationId(@Param("id") Integer id, @Param("organizationId") Integer organizationId);

    @Query("SELECT c FROM Channel c WHERE (c.organizationId = :organizationId AND c.categoryId = :categoryId) ORDER BY c.displayOrder DESC")
    Channel findLatestOrder(@Param("organizationId") Integer organizationId, @Param("categoryId") Integer categoryId);
}