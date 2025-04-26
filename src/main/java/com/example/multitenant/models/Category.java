package com.example.multitenant.models;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.multitenant.dtos.categories.*;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "categories", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"organization_id", "name"})
})
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Column(name = "organization_id", nullable = false)
    private Integer organizationId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "organization_id", insertable = false, updatable = false)
    private Organization organization;

    @OneToMany(mappedBy = "category", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Channel> channels;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "categories_roles",
        joinColumns = @JoinColumn(name = "category_id", referencedColumnName = "id", table = "categories"),
        inverseJoinColumns = @JoinColumn(name = "role_id",  referencedColumnName = "id", table = "organization_roles")
    )
    private Set<OrganizationRole> authorizedRoles = new HashSet<OrganizationRole>();

    @Column(name = "created_by_id", insertable = false, updatable = false)
    private Long createdById;
    
    @Column(name = "last_modified_by_id", insertable = false, updatable = false)
    private Long lastModifiedById;
    
    @CreatedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", updatable = false)
    private User createdBy;

    @LastModifiedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_modified_by_id")
    private User lastModifiedBy;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    public CategoryViewDTO toViewDTO() {
        return new CategoryViewDTO(this);
    }
}
