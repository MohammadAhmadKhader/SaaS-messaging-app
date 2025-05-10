package com.example.multitenant.models;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.multitenant.dtos.organizationrestrictions.OrgRestrictionViewDTO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "organization_restrictions", indexes = {
    @Index(name = "idx_organization_restrictions_user_id_organization_id", columnList = "user_id, organization_id"),
})
public class OrganizationRestriction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "organization_id", nullable = false)
    private Integer organizationId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, optional = false)
    @JoinColumn(name = "organization_id", updatable = false, insertable = false)
    private Organization organization;

    @Column(name = "user_id", updatable = false, insertable = false)
    private Long userId;
    
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "until", nullable = false)
    private Instant until;

    @Column(name = "reason", length = 128)
    private String reason;

    @CreationTimestamp
    private Instant createdAt;
    
    @CreatedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    public OrgRestrictionViewDTO toViewDTO() {
        return new OrgRestrictionViewDTO(this);
    }
}