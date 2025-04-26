package com.example.multitenant.dtos.auth;

import com.example.multitenant.models.User;
import com.fasterxml.jackson.annotation.*;

public abstract class UserPrincipalMixin {
    @JsonCreator
    public UserPrincipalMixin(@JsonProperty("user") User user) {
    }
}