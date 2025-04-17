package com.example.multitenant.services.users;

import org.springframework.stereotype.Component;

import com.example.multitenant.models.User;
import com.example.multitenant.utils.ServicesHelper;

@Component
public class UsersServicesHelper extends ServicesHelper<User> {
    public UsersServicesHelper() {
        super(User.class);
    }
}
