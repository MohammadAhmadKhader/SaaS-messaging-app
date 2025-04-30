package com.example.multitenant.repository;

import java.util.List;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import com.example.multitenant.models.OrgMessage;

import org.springframework.data.repository.query.Param;
import jakarta.transaction.Transactional;

@Repository
public interface OrgMessagesRepository extends GenericRepository<OrgMessage, Integer> {
    @Query("SELECT m FROM OrgMessage m WHERE m.senderId = :senderId")
    List<OrgMessage> findBySenderId(@Param("senderId") Integer senderId);

    @Query("""
        SELECT m FROM OrgMessage m 
        WHERE (m.id = :id AND m.senderId = :senderId)
    """)
    OrgMessage findByIdAndSenderId(@Param("id") Integer id, @Param("senderId") Long senderId);

    @Transactional
    @Modifying
    @Query("DELETE FROM OrgMessage m WHERE m.id = :id AND m.senderId = :senderId")
    void deleteByIdAndSenderId(@Param("id") Integer id, @Param("senderId") Long senderId);
}
