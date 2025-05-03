package com.example.multitenant.models;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.multitenant.dtos.messages.*;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "organization_messages", indexes = {
    @Index(name = "idx_organization_message_organization_id", columnList = "organization_id"),
    @Index(name = "idx_organization_message_sender_id", columnList = "sender_id")
})
public class OrgMessage extends BaseMessage {

    @Column(name = "organization_id", nullable = false)
    private Integer organizationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false, updatable = false, insertable = false)
    private Organization organization;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "channel_id", nullable = false, updatable = false, insertable = false)
    private Channel channel;

    @Column(name = "channel_id",nullable = false)
    private Integer channelId;

    @PrePersist
    private void loadDefaults() {
        this.setIsUpdated(false);
    }

    public OrgMessageViewDTO toViewDTO() {
        return new OrgMessageViewDTO(this);
    }

    public OrgMessageWithUserViewDTO toViewDTOWithUser() {
        return new OrgMessageWithUserViewDTO(this);
    }
}
