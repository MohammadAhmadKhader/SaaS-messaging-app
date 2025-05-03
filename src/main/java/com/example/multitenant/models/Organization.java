package com.example.multitenant.models;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.multitenant.dtos.organizations.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "organizations", indexes = {
    @Index(name ="idx_organization_owner_id", columnList = "owner_id")
})
@Entity
public class Organization implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, unique = true, length = 128)
    private String name;

    @Column(name = "industry", nullable = false, length = 128)
    private String industry;

    @Column(name = "image_url", nullable = true, length = 256)
    private String imageUrl;

    @CreationTimestamp
    private Instant createdAt;

    @CreatedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = true)
    private User owner;

    @OneToMany(mappedBy = "organization", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @OrderBy("joinedAt DESC, id.organizationId ASC, id.userId ASC")
    private List<Membership> memberships = new ArrayList<>();

    @OneToMany(mappedBy = "organization", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<OrganizationRole> roles = new ArrayList<>();

    @OneToMany(mappedBy = "organization", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @OrderBy("displayOrder ASC")
    private List<Category> categories = new ArrayList<>();


    @OneToMany(mappedBy = "organization", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @OrderBy("displayOrder ASC")
    private List<Channel> channels = new ArrayList<>();


    public OrganizationViewDTO toViewDTO() {
        return new OrganizationViewDTO(this);
    }

    public Organization(String name, String industry) {
        setName(name);
        setIndustry(industry);
    }
}
