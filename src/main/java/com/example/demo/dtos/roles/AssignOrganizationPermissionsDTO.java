package com.example.demo.dtos.roles;

import java.util.Set;

import lombok.Getter;

@Getter
public class AssignOrganizationPermissionsDTO {
    Set<Integer> permissionsIds;
}
