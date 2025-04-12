package com.example.multitenant.services.ownership.contract;

import java.io.Serializable;

import com.example.multitenant.models.Organization;
import com.example.multitenant.models.User;

public interface OwnershipEntity<TMode, TPrimaryKey extends Serializable> {
    void setId(TPrimaryKey id);
    Long getUserId();
    void setOrganization(Organization org);
    Organization getOrganization();
    void setUser(User user);
}
