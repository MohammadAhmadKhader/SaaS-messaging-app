package com.example.demo.services.ownership.contract;

import com.example.demo.models.Organization;

public interface OrganizationOwnershipEntity {
    void setOrganization(Organization organization);
    Organization getOrganization();
    Integer getOrganizationId();
}
