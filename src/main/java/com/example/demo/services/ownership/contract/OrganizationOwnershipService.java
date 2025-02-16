package com.example.demo.services.ownership.contract;

public interface OrganizationOwnershipService<TModel, TPrimaryKey> {
    TModel updateByOrganization(TPrimaryKey id, TModel content, Integer orgId);
    void deleteByOrganization(TPrimaryKey id, TModel content, Integer orgId);
}
