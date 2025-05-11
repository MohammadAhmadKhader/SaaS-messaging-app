package com.example.multitenant.models;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.Check;
import org.hibernate.annotations.Checks;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.example.multitenant.dtos.users.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@Table(name = "users")
@Entity
@Checks(
    value = {
        @Check(name = "chk_users_email_lowercase", constraints = "email = lower(email)"),
        @Check(
            name = "chk_fields_nullability_based_on_deletion",
            constraints = """
                (
                    is_deleted = false AND 
                    email IS NOT NULL AND 
                    first_name IS NOT NULL AND 
                    last_name IS NOT NULL AND 
                    password IS NOT NULL
                )
                OR
                (   
                    is_deleted = true AND
                    email IS NULL AND 
                    first_name IS NULL AND 
                    last_name IS NULL AND 
                    password IS NULL AND
                    avatar_url IS NULL
                ) 
            """
        )
    }
)
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name ="email", length = 64, unique = true)
    private String email;

    @JsonIgnore
    @Column(name= "password", length = 60)
    private String password;

    @Column(name = "first_name", length = 64)
    private String firstName;

    @Column(name = "last_name", length = 64)
    private String lastName;

    @Column(name = "avatar_url", length = 256)
    private String avatarUrl;

    @CreationTimestamp
    private Instant createdAt;

    @Column(name = "is_deleted", nullable = false)
    @ColumnDefault("false")
    private boolean deleted;

    @UpdateTimestamp
    private Instant updatedAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "users_global_roles",
        joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id", table = "users"),
        inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id", table = "global_roles")
    )
    @OrderBy("id ASC")
    private Set<GlobalRole> roles = new HashSet<>();

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

    public void setEmail(String email) {
        this.email = email != null ? email.toLowerCase() : null;
    }

    @PrePersist
    private void loadDefaults() {
        this.email = this.email.toLowerCase();
    }

    public UserOrgViewDTO toOrganizationViewDTO(Membership membership) {
        return new UserOrgViewDTO(this, membership);
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

    public UserSearchDTO toSearchDTO() {
        return new UserSearchDTO(this);
    }
}