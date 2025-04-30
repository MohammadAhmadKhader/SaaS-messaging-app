package com.example.multitenant.repository.logsrepositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.multitenant.models.logsmodels.AuthLog;
import com.example.multitenant.models.logsmodels.BaseLog;
import com.example.multitenant.repository.GenericRepository;


@Repository
public interface BaseLogsRepository extends GenericRepository<BaseLog, UUID> {
    @Query("""
        SELECT a FROM BaseLog a
        LEFT JOIN FETCH TREAT(a AS AuthLog).user
    """)
    List<BaseLog> findAllAuthLogs();

    @Query("""
        SELECT a FROM BaseLog a
        LEFT JOIN FETCH TREAT(a AS InvitationsLog).inviter inviter
        WHERE TYPE(a) = InvitationsLog AND TREAT(a AS InvitationsLog).organizationId = :organizationId
          AND inviter IS NOT NULL
    """)
    List<BaseLog> findAllOrgLogs(@Param("organizationId") Integer organizationId);
}
