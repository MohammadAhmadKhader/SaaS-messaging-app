package com.example.multitenant.models;

import java.time.Instant;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.multitenant.dtos.channels.*;
import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "channels", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"category_id", "name"})
})
public class Channel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, length = 32)
    private String name;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Column(name = "organization_id", nullable = false)
    private Integer organizationId;

    @Column(name = "category_id", nullable = false)
    private Integer categoryId;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "organization_id", insertable = false, updatable = false)
    private Organization organization;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private Category category;

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

    @OneToMany(mappedBy = "channel", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<OrgMessage> messages; 

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    public ChannelViewDTO toViewDTO() {
        return new ChannelViewDTO(this);
    }

    public ChannelWithOrgMessagesViewDTO toViewDTOWithMessages() {
        return new ChannelWithOrgMessagesViewDTO(this);
    }
}