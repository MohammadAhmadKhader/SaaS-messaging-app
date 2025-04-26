package com.example.multitenant.models.binders;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Embeddable
public class RolePermissionKey {
    @Column(name = "role_id")
    private Integer roleId;

    @Column(name = "permission_id")
    private Integer permissionId;
}
