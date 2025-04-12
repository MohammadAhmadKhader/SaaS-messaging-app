package com.example.demo.models;

import java.time.Instant;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.example.demo.models.binders.OrganizationMembershipKey;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@Entity(name = "organization_membership")
public class OrganizationMembership {
    @EmbeddedId
    private OrganizationMembershipKey id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("organizationId")
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @ManyToMany
    @JoinTable(
        name = "membership_roles",
        joinColumns = {
            @JoinColumn(name = "organization_id"),
            @JoinColumn(name = "user_id")
        },
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<OrganizationRole> organizationRoles;

    @CreationTimestamp
    private Instant joinedAt;
}
