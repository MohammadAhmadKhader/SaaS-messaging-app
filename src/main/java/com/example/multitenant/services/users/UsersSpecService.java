package com.example.multitenant.services.users;

import org.springframework.stereotype.Service;

import com.example.multitenant.models.User;
import com.example.multitenant.services.helperservices.SpecificationsService;

@Service
public class UsersSpecService extends SpecificationsService<User, Long> {
    public UsersSpecService() {
        super(User.class);
    }
}
