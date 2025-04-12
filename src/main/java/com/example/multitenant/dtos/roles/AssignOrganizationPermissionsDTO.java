package com.example.multitenant.dtos.roles;

import java.util.Set;

import lombok.Getter;

@Getter
public class AssignOrganizationPermissionsDTO {
    Set<Integer> permissionsIds;
}
