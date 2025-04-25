package com.example.multitenant.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.multitenant.models.Message;

import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;

@Repository
public interface MessagesRepository extends GenericRepository<Message, Integer> {
    @Query("SELECT m FROM Message m WHERE m.senderId = :senderId")
    List<Message> findBySenderId(@Param("senderId") Integer senderId);

    @Query("""
        SELECT m FROM Message m 
        WHERE (m.id = :id AND m.senderId = :senderId)
    """)
    Message findByIdAndSenderId(@Param("id") Integer id, @Param("senderId") Long senderId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Message m WHERE m.id = :id AND m.senderId = :senderId")
    void deleteByIdAndSenderId(@Param("id") Integer id, @Param("senderId") Long senderId);
}
