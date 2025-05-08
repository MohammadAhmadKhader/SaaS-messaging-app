package com.example.multitenant.services.security;

import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import com.example.multitenant.dtos.auth.UserPrincipal;
import com.example.multitenant.exceptions.AppLockedException;
import com.example.multitenant.services.cache.LoginAttemptsCacheService;
import com.example.multitenant.services.users.UsersService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsersService usersService;
    private final LoginAttemptsCacheService loginAttemptsCacheService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var lowerCaseEmail = email.toLowerCase();
        if(loginAttemptsCacheService.isLocked(lowerCaseEmail)) {
            throw new AppLockedException(lowerCaseEmail);
        }

        var user = this.usersService.findOneByEmailWithRolesAndPermissions(lowerCaseEmail);
        if(user == null) {
           throw new UsernameNotFoundException("email or password is wrong");
        }

        return new UserPrincipal(user);
    }
}
