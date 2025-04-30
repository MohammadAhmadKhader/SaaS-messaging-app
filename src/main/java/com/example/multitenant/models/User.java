package com.example.multitenant.models;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.example.multitenant.dtos.users.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "users")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name ="email", nullable = false, length = 64)
    private String email;

    @JsonIgnore
    @Column(name= "password",nullable = false, length = 60)
    private String password;

    @Column(name = "first_name",  nullable = false, length = 64)
    private String firstName;

    @Column(name = "last_name",  nullable = false, length = 64)
    private String lastName;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "users_global_roles",
        joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id", table = "users"),
        inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id", table = "global_roles")
    )
    @OrderBy("id ASC")
    List<GlobalRole> roles = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Membership> memberships = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_friends", 
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "friend_id"),
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "friend_id"})
    )
    private Set<User> friends = new HashSet<User>();
    
    // this is the other side of the relation of user - friends
    // think of this as "who considers me as a friend" thats why its called "friendOf"
    @ManyToMany(mappedBy = "friends", fetch = FetchType.LAZY)
    private Set<User> friendOf = new HashSet<User>();

    public User(String email, String firstName, String lastName, String password) {
        setEmail(email);
        setFirstName(firstName);
        setLastName(lastName);
        setPassword(password);
    }

    public String getFullName() {
        return this.getFirstName()+ " " + this.getLastName();
    }
    
    public UserViewDTO toViewDTO() {
        return new UserViewDTO(this);
    }

    public UserOrganizationViewDTO toOrganizationViewDTO(Membership membership) {
        return new UserOrganizationViewDTO(this, membership);
    }

    public UserWithoutRolesViewDTO toViewWithoutRolesDTO() {
        return new UserWithoutRolesViewDTO(this);
    }

    public UserWithoutPermissionsViewDTO toUserWithoutPermissionsViewDTO() {
        return new UserWithoutPermissionsViewDTO(this);
    }

    public UserMessageViewDTO toUserMessageViewDTO() {
        return new UserMessageViewDTO(this);
    }
}