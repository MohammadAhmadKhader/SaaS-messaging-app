package com.example.multitenant.dtos.users;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

import com.example.multitenant.dtos.globalroles.GlobalRoleViewDTO;
import com.example.multitenant.dtos.organizationroles.OrganizationRoleViewDTO;
import com.example.multitenant.models.Membership;
import com.example.multitenant.models.OrganizationRole;
import com.example.multitenant.models.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserOrganizationViewDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private Instant createdAt;
    private Instant updatedAt;
    private List<OrganizationRoleViewDTO> roles;

    public UserOrganizationViewDTO(User user, Membership membership) {
        setId(user.getId());
        setFirstName(user.getFirstName());
        setLastName(user.getLastName());
        setEmail(user.getEmail());
        setCreatedAt(user.getCreatedAt());
        setUpdatedAt(user.getUpdatedAt());
        setRoles(membership.getOrganizationRoles().stream().map((r) -> r.toViewDTO()).toList());
    }

    public UserOrganizationViewDTO() {
        
    }
}
