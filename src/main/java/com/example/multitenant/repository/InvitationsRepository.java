package com.example.multitenant.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.multitenant.models.Invitation;

@Repository
public interface InvitationsRepository extends GenericRepository<Invitation, Integer> {
    @Query("SELECT inv FROM Invitation inv WHERE (inv.recipient.id = :recipientId OR inv.sender.id = :recipientId) AND inv.id < :cursor ORDER BY inv.id DESC")
    List<Invitation> findByRecipientIdAndCursor(Long recipientId, Integer cursor, Pageable pageable);

    @Query("SELECT inv FROM Invitation inv WHERE (inv.recipient.id = :recipientId OR inv.sender.id = :recipientId) ORDER BY inv.id DESC")
    List<Invitation> findByRecipientId(Long recipientId, Pageable pageable);

    @Query("SELECT inv FROM Invitation inv WHERE inv.organization.id = :organizationId AND inv.id < :cursor ORDER BY inv.id DESC")
    List<Invitation> findByOrganizationIdAndCursor(Integer organizationId, Integer cursor, Pageable pageable);

    @Query("SELECT inv FROM Invitation inv WHERE inv.organization.id = :organizationId ORDER BY inv.id DESC")
    List<Invitation> findByOrganizationId(Integer organizationId, Pageable pageable);
}