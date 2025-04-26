package com.example.multitenant.models;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "content", nullable = false)
    private String content;

    @CreatedBy
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Column(name = "sender_id", nullable = false, insertable = false, updatable = false)
    private Long senderId;

    @Column(name = "is_updated", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isUpdated;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}