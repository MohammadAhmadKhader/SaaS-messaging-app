package com.example.demo.services.ownership.contract;

import java.io.Serializable;

import com.example.demo.models.Organization;
import com.example.demo.models.User;

public interface OwnershipEntity<TMode, TPrimaryKey extends Serializable> {
    void setId(TPrimaryKey id);
    Long getUserId();
    void setOrganization(Organization org);
    Organization getOrganization();
    void setUser(User user);
}
