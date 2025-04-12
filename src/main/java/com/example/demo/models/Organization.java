package com.example.demo.models;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.example.demo.dtos.organizations.OrganizationViewDTO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "organizations")
public class Organization implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "industry", nullable = false)
    private String industry;

    @CreationTimestamp
    private Instant createdAt;

    // @OneToMany(cascade = CascadeType.ALL, mappedBy = "organization")
    // private List<User> users = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "organization")
    private List<Content> contents = new ArrayList<>();

    public OrganizationViewDTO toViewDTO() {
        return new OrganizationViewDTO(this);
    }

    public Organization(String name, String industry) {
        setName(name);
        setIndustry(industry);
    }
}
