package com.example.multitenant.dtos.auth;

import java.io.Serializable;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.multitenant.dtos.users.UserViewDTO;
import com.example.multitenant.models.User;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

public class UserPrincipal implements UserDetails {
    final private User user;

    @JsonCreator
    public UserPrincipal(@JsonProperty User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        var permissions = this.user.getRoles().stream().flatMap((role) -> {
            return role.getPermissions().stream();
        }).map((perm) -> {
            return new SimpleGrantedAuthority(perm.getName());
        }).toList();

        return permissions;
    }

    @Override
    public String getPassword() {
        return this.user.getPassword();
    }

    @Override
    public String getUsername() {
        return this.user.getEmail();
    }

    public User getUser(){
        return this.user;
    }
}
