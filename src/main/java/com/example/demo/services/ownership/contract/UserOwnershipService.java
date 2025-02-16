package com.example.demo.services.ownership.contract;

import java.io.Serializable;

public interface UserOwnershipService<TModel extends UserOwnershipEntity<TModel, TPrimaryKey>, TPrimaryKey extends Serializable> {
    TModel createOwn(TModel content);
    TModel updateOwn(TPrimaryKey id, TModel content);
    void deleteOwn(TPrimaryKey id);
}
