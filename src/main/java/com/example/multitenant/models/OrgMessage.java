package com.example.multitenant.models;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.multitenant.dtos.messages.*;

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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "organization_messages")
public class OrgMessage extends Message {

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

    public OrgMessageViewDTO toViewDTO() {
        return new OrgMessageViewDTO(this);
    }

    public OrgMessageWithUserViewDTO toViewDTOWithUser() {
        return new OrgMessageWithUserViewDTO(this);
    }
}
