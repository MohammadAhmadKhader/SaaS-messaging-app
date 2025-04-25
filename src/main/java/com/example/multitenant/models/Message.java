package com.example.multitenant.models;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.multitenant.dtos.messages.MessageViewDTO;
import com.example.multitenant.dtos.messages.MessageWithUserViewDTO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "messages")
public class Message {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "organization_id", nullable = false)
    private Integer organizationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false, updatable = false, insertable = false)
    private Organization organization;

    @CreatedBy
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "channel_id", nullable = false, updatable = false, insertable = false)
    private Channel channel;

    @Column(name = "channel_id",nullable = false)
    private Integer channelId;
  
    @Column(name = "sender_id", nullable = false, insertable = false, updatable = false)
    private Long senderId;

    @Column(name = "is_updated", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isUpdated;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    public MessageViewDTO toViewDTO() {
        return new MessageViewDTO(this);
    }

    public MessageWithUserViewDTO toViewDTOWithUser() {
        return new MessageWithUserViewDTO(this);
    }
}
