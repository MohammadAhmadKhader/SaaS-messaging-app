package com.example.multitenant.services.users;

import org.springframework.stereotype.Service;

import com.example.multitenant.models.User;
import com.example.multitenant.services.helperservices.ServiceSpecifications;

@Service
public class UsersSpecificationsService extends ServiceSpecifications<User, Long> {
    public UsersSpecificationsService() {
        super(User.class);
    }
}
