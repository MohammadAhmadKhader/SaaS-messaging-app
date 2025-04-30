package com.example.multitenant.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.multitenant.models.FriendRequest;
import com.example.multitenant.models.enums.FriendRequestStatus;

import org.springframework.data.repository.query.Param;

@Repository
public interface FriendRequestsRepository extends GenericRepository<FriendRequest, Integer> {
    @Query("""
        SELECT fr FROM FriendRequest fr 
        WHERE fr.receiver.id = :receiverId 
        AND (:cursorId IS NULL OR fr.id < :cursorId)
        ORDER BY fr.createdAt DESC, fr.id DESC
    """)
    List<FriendRequest> findFriendRequestsByReceiver(@Param("receiverId") Long receiverId, @Param("cursorId") Integer cursorId, Pageable pageable);

    @Query("""
        SELECT fr FROM FriendRequest fr 
        WHERE fr.sender.id = :senderId 
        AND (:cursorId IS NULL OR fr.id < :cursorId)
        ORDER BY fr.createdAt DESC, fr.id DESC
    """)
    List<FriendRequest> findFriendRequestsBySender(@Param("senderId") Long senderId, @Param("cursorId") Integer cursorId, Pageable pageable);

    @Query("""
        SELECT fr FROM FriendRequest fr 
        LEFT JOIN FETCH fr.sender sender
        LEFT JOIN FETCH fr.receiver receiver
        WHERE fr.id = :id
    """)
    FriendRequest findOneWithUsers(@Param("id") Integer id);

    @Query("""
        SELECT COUNT(fr) > 0 
        FROM FriendRequest fr 
        WHERE (fr.sender.id = :senderId AND fr.receiver.id = :receiverId AND fr.status = :status)
        OR (fr.sender.id = :receiverId AND fr.receiver.id = :senderId AND fr.status = :status)
    """)
    boolean existsFriendRequest(@Param("senderId") Long senderId, @Param("receiverId") Long receiverId, @Param("status") FriendRequestStatus status);
}
