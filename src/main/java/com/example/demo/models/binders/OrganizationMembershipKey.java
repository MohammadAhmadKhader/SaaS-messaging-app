package com.example.demo.models.binders;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class OrganizationMembershipKey {
    @Column(name = "organization_id")
    private Integer organizationId;

    @Column(name = "user_id")
    private Long userId;
}
