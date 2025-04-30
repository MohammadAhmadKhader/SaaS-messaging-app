package com.example.multitenant.models.binders;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class MembershipKey implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "organization_id")
    private Integer organizationId;

    @Column(name = "user_id")
    private Long userId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MembershipKey that = (MembershipKey) o;
        return Objects.equals(userId, that.userId) && 
               Objects.equals(organizationId, that.organizationId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(userId, organizationId);
    }
}
