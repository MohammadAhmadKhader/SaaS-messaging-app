package com.example.demo.services.ownership.contract;

import java.io.Serializable;

import com.example.demo.models.User;

public interface UserOwnershipEntity<TMode, TPrimaryKey extends Serializable> {
    void setId(TPrimaryKey id);
    Long getUserId();
    void setOrganizationId(Integer orgId);
    void setUser(User user);
}
