package com.example.multitenant.services.security;

import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import com.example.multitenant.dtos.auth.UserPrincipal;
import com.example.multitenant.services.users.UsersService;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsersService usersService;
    public CustomUserDetailsService(UsersService usersService) {
        this.usersService = usersService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var user = this.usersService.findByEmail(email);
        if(user == null) {
           throw new UsernameNotFoundException("Email or password is wrong");
        }

        return new UserPrincipal(user);
    }
}
