package com.example.multitenant.models;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.hibernate.annotations.CreationTimestamp;

import com.example.multitenant.dtos.invitations.InvitationViewDTO;
import com.example.multitenant.models.enums.InvitationStatus;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@Table(name = "invitations")
public class Invitation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "organization_id", insertable = false, updatable = false)
    private Integer organizationId;

    @ManyToOne
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Column(name = "sender_id", insertable = false, updatable = false)
    private Long senderId;

    @Column(name = "recipient_id", insertable = false, updatable = false)
    private Long recipientId;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @Enumerated(EnumType.STRING)
    @Column
    private InvitationStatus status;

    @CreationTimestamp
    private Instant createdAt;

    public Invitation() {
        
    }

    public void loadDefaults() {
        setStatus(InvitationStatus.PENDING);
    }

    public InvitationViewDTO toViewDTO() {
        return new InvitationViewDTO(this);
    }
}
