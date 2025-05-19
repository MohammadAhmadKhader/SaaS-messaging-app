package com.example.multitenant.testsupport.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.example.multitenant.dtos.auth.UserPrincipal;
import com.example.multitenant.repository.UsersRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TestAuthHelpers {
    private final UsersRepository usersRepository;
    public void setMockUser(Long userId, List<String> roles, List<String> permissions) {
        var user = this.usersRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new IllegalStateException("user was not found");
        }
    
        var principal = new UserPrincipal(user);
        var authorities = new ArrayList<GrantedAuthority>();
    
        if(roles != null) {
            for (var role : roles) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
            }
        }
        
        if(permissions != null) {
            for (var permission : permissions) {
                authorities.add(new SimpleGrantedAuthority(permission));
            }   
        }
    
        var auth = new UsernamePasswordAuthenticationToken(principal, "password", authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
