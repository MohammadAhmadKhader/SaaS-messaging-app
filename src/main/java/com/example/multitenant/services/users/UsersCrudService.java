package com.example.multitenant.services.users;

import org.springframework.stereotype.Service;

import com.example.multitenant.models.User;
import com.example.multitenant.repository.UsersRepository;
import com.example.multitenant.services.helperservices.GenericCrudService;

@Service
public class UsersCrudService extends GenericCrudService<User, Long> {
    private UsersRepository usersRepository;
    public UsersCrudService(UsersRepository usersRepository) {
        super(usersRepository);
        this.usersRepository = usersRepository;
    }
}
