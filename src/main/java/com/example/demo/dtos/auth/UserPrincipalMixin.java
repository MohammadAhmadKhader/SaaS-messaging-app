package com.example.demo.dtos.auth;

import com.example.demo.models.User;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class UserPrincipalMixin {
    @JsonCreator
    public UserPrincipalMixin(@JsonProperty("user") User user) {
    }
}