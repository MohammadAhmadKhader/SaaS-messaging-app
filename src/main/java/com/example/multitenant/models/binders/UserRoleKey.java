package com.example.multitenant.models.binders;

import java.io.Serializable;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Embeddable
public class UserRoleKey implements Serializable{
    @Column(name = "user_Id")
    Long userId;

    @Column(name = "role_id")
    Integer roleId;
}
