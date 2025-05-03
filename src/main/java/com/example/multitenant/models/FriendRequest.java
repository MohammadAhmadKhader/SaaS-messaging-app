package com.example.multitenant.models;

import java.time.Instant;

import com.example.multitenant.dtos.friendrequests.FriendRequestViewDTO;
import com.example.multitenant.models.enums.FriendRequestStatus;
import org.hibernate.annotations.*;
import jakarta.persistence.*;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.*;

/**
 * user nullable is true in case user has deleted himself.
 */
@Getter
@Setter
@Entity
@Table(name = "friend_requests", indexes = {
    @Index(name = "idx_friend_requests_sender_id", columnList = "sender_id"),
    @Index(name = "idx_friend_requests_receiver_id", columnList = "receiver_id")
})
public class FriendRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "receiver_id", nullable = true, insertable = false, updatable = false)
    private Long receiverId;

    @Column(name = "sender_id", nullable = true, insertable = false, updatable = false)
    private Long senderId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id", nullable = true)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receiver_id", nullable = true)
    private User receiver;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private FriendRequestStatus status;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "responded_at")
    private Instant respondedAt;

    @PrePersist
    private void prePersist() {
        if (status == null) {
            status = FriendRequestStatus.PENDING;
        }
    }

    public FriendRequestViewDTO toViewDTO() {
        return FriendRequestViewDTO.fromEntity(this);
    }
}