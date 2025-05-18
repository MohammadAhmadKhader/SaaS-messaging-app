package com.example.multitenant.testsupport.annotations;

import java.util.ArrayList;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.stereotype.Component;

import com.example.multitenant.dtos.auth.UserPrincipal;
import com.example.multitenant.models.User;
import com.example.multitenant.services.users.UsersService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {
    private final UsersService usersService;
    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        
        var user = this.usersService.findByEmail(customUser.username());
        if(user == null) {
            throw new IllegalStateException(String.format("user with email %s is not found", customUser.username()));
        }
        
        UserPrincipal principal = new UserPrincipal(user);
        
        var authorities = new ArrayList<GrantedAuthority>();
        for (String role : customUser.roles()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        }

        for (String authority : customUser.authorities()) {
            authorities.add(new SimpleGrantedAuthority(authority));
        }
        
        var auth = new UsernamePasswordAuthenticationToken(principal, "password", authorities);
        context.setAuthentication(auth);
        return context;
    }
}
