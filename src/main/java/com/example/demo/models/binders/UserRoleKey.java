package com.example.demo.models.binders;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class UserRoleKey implements Serializable{
    @Column(name = "user_Id")
    Long userId;

    @Column(name = "role_id")
    Integer roleId;
}
