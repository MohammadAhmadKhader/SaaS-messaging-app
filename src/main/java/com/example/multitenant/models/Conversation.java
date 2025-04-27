package com.example.multitenant.models;

import java.time.Instant;
import java.util.List;

import org.hibernate.annotations.Check;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.*;
import lombok.*;

/*
 * we force ascending between user_1_id and user_2_id 
 * order to ensure correct implementation 
 */
@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name = "conversations", uniqueConstraints = {
    @UniqueConstraint(name = "unique_user_pairs", columnNames = {"user_1_id","user_2_id"})
},
indexes = {
    @Index(name = "idx_user_1_id", columnList = "user_1_id"),
    @Index(name = "idx_user_2_id", columnList = "user_2_id"),
})
@Check(constraints = "user_1_id < user_2_id")  
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_1_id")
    private User user1;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_2_id")
    private User user2;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "conversation", cascade = CascadeType.REMOVE)
    private List<ConversationMessage> messages;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    private ConversationMessage lastMessage;

    @Column(name = "is_hidden", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isHidden;

    @CreationTimestamp
    private Instant createdAt;

    @PreUpdate
    private void validateUserIds() {
        if (user1 != null && user2 != null) {
            if (user1.getId() > user2.getId()) {
                throw new IllegalArgumentException("user_1_id must be less than user_2_id");
            }
        }
    }

    @PrePersist
    public void setHiddenValue() {
        isHidden = false;

        if (user1 != null && user2 != null) {
            if (user1.getId() > user2.getId()) {
                throw new IllegalArgumentException("user_1_id must be less than user_2_id");
            }
        }
    }

    public void setUsersInOrder() {
        if (user1.getId() > user2.getId()) {
            var temp = user1;
            user1 = user2;
            user2 = temp;
        }
    }
}