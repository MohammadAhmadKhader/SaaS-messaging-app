package com.example.multitenant.models.binders;

import java.io.Serializable;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Embeddable
public class MembershipKey implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "organization_id")
    private Integer organizationId;

    @Column(name = "user_id")
    private Long userId;

    public MembershipKey(Integer organizationId, Long userId) {
        this.organizationId = organizationId;
        this.userId = userId;
    }

    public MembershipKey() {
        
    }
}
