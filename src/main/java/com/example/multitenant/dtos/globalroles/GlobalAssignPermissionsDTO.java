package com.example.multitenant.dtos.globalroles;

import java.util.Set;

import lombok.Getter;

@Getter
public class GlobalAssignPermissionsDTO {
    Set<Integer> permissionsIds;
}
