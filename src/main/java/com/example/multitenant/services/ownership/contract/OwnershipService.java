package com.example.multitenant.services.ownership.contract;

import java.io.Serializable;

public interface OwnershipService<TModel extends OwnershipEntity<TModel, TPrimaryKey>, TPrimaryKey extends Serializable> {
    TModel createOwn(TModel content, Integer tenantId);
    TModel updateOwn(TPrimaryKey id, TModel content, Integer tenantId);
    void deleteOwn(TPrimaryKey id, Integer tenantId);
}
