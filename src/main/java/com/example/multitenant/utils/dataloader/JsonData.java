package com.example.multitenant.utils.dataloader;

import java.util.List;

import com.example.multitenant.models.*;
import lombok.*;

@Getter
public class JsonData {
    List<OrgPermission> organizationPermissions;
    List<OrgRole> organizationRoles;
    List<GlobalRole> globalRoles;
    List<GlobalPermission> globalPermissions;
    List<Organization> organizations;
    List<User> users;
}