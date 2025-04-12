package com.example.multitenant.models;

import java.time.Instant;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.ManyToAny;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

// @Getter
// @Setter
// public class Resource {
//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Integer id;

//     private String name;

//     private String type;

//     @ManyToOne
//     @JoinColumn(name = "owner_id", nullable = false)
//     private User owner;

//     @ManyToOne
//     @JoinColumn(name = "organization_id", nullable = false)
//     private Organization organization;

//     @Column(name = "description")
//     private String description;

//     @CreationTimestamp
//     private Instant createdAt;

//     @UpdateTimestamp
//     private Instant updatedAt;
// }
