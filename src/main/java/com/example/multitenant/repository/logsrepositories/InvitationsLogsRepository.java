package com.example.multitenant.repository.logsrepositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.multitenant.models.logsmodels.InvitationsLog;
import com.example.multitenant.repository.GenericRepository;

@Repository
public interface InvitationsLogsRepository extends GenericRepository<InvitationsLog, UUID> {
    @Query("""
        SELECT i FROM InvitationsLog i
        LEFT JOIN FETCH i.inviter inviter
        WHERE i.organizationId = :organizationId AND inviter IS NOT NULL
    """)
    List<InvitationsLog> findAllInvitationLogs(@Param("organizationId") Integer organizationId);
}
