package com.example.multitenant.models.logsmodels;

import com.example.multitenant.models.Organization;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseOrganizationsLogs extends BaseLog {
    @Column(name = "organization_id")
    private Integer organizationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", updatable = false, insertable = false)
    private Organization organization;
}
