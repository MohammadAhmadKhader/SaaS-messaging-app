package com.example.demo.models;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.example.demo.dtos.users.UserViewDTO;
import com.example.demo.dtos.users.UserWithoutPermissionsViewDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(
name = "users",
indexes = {
    @Index(name = "idx_email_organizationid", columnList = "email, organization_id", unique = true)
})
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Email
    @Column(nullable = false)
    private String email;

    @JsonIgnore
    @Column(name= "password",nullable = false)
    private String password;

    @Column(name = "first_name",  nullable = false)
    private String firstName;

    @Column(name = "last_name",  nullable = false)
    private String lastName;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    List<Content> contents = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
        name = "users_global_roles",
        joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id", table = "users"),
        inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id", table = "global_roles")
    )
    List<GlobalRole> roles = new ArrayList<>();

    public User(String email, String firstName, String lastName, String password) {
        setEmail(email);
        setFirstName(firstName);
        setLastName(lastName);
        setPassword(password);
    }
    
    public UserViewDTO toViewDTO() {
        return new UserViewDTO(this);
    }

    public UserWithoutPermissionsViewDTO toUserWithoutPermissionsViewDTO() {
        return new UserWithoutPermissionsViewDTO(this);
    }
}
