package com.example.demo.models;

import java.time.Instant;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.UpdateTimestamp;

import com.example.demo.dtos.contents.ContentViewDTO;
import com.example.demo.dtos.shared.IViewDTO;
import com.example.demo.models.enums.ContentType;
import com.example.demo.models.enums.Status;
import com.example.demo.services.ownership.contract.OrganizationOwnershipEntity;
import com.example.demo.services.ownership.contract.UserOwnershipEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "contents")
public class Content implements IViewDTO<ContentViewDTO>, UserOwnershipEntity<Content, Integer>, OrganizationOwnershipEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Id in db
    Integer id;

    @Column(name = "title", nullable = false)
    String title;

    @Column(name = "description", nullable = false)
    String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    Status status = Status.IDEA;

    @Enumerated(EnumType.STRING)
    @Column(name = "content_type", nullable = false)
    ContentType contentType;

    @CreationTimestamp
    Instant createdAt;

    @UpdateTimestamp
    Instant updatedAt;
    
    @Column(name = "user_id", insertable = false, updatable = false)
    Long userId;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName="id")
    User user;

    @Column(name = "url", nullable = false)
    String url;

    @Column(name = "organization_id", insertable = false, updatable = false)
    Integer organizationId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "organization_id")
    Organization organization;

    public Content(String title, String desc, Status status, ContentType contentType, String url) {
        setTitle(title);
        setDescription(desc);
        setStatus(status);
        setContentType(contentType);
        setUrl(url);
    }

    public ContentViewDTO toViewDTO() {
        return new ContentViewDTO(id, title, description, status, contentType, createdAt, updatedAt, user == null ? null : user.toUserWithoutPermissionsViewDTO(), url);
    }
} 
