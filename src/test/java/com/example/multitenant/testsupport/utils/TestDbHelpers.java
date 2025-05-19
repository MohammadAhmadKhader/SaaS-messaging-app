package com.example.multitenant.testsupport.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.example.multitenant.dtos.auth.UserPrincipal;
import com.example.multitenant.models.Membership;
import com.example.multitenant.models.binders.MembershipKey;
import com.example.multitenant.repository.*;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TestDbHelpers {
    private final MembershipRepository membershipRepository;
    private final UsersRepository usersRepository;
    private final OrgsRepository orgsRepository;

    public void addUserToOrganization(Long userId, Integer orgId) {
        if(userId == null || userId.equals(0L)) {
            throw new IllegalStateException("userId received as null or 0");
        }

        if(orgId == null || orgId.equals(0)) {
            throw new IllegalStateException("organizationId received as null or 0");
        }

        var user = usersRepository.findById(userId).orElse(null);
        if(user == null) {
            throw new IllegalStateException("user was not found");
        }

        var org = orgsRepository.findById(orgId).orElse(null);
        if(org == null) {
            throw new IllegalStateException("organization was not found");
        }

        var membership = new Membership(org.getId(), user.getId());
        membership.loadDefaults();

        this.membershipRepository.save(membership);
    }
}