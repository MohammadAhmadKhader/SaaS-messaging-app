package com.example.multitenant.dtos.auth;

import com.example.multitenant.models.User;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class UserPrincipalMixin {
    @JsonCreator
    public UserPrincipalMixin(@JsonProperty("user") User user) {
    }
}