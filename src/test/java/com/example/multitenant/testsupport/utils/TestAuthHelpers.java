package com.example.multitenant.testsupport.utils;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.example.multitenant.dtos.auth.LoginDTO;
import com.example.multitenant.dtos.auth.UserPrincipal;
import com.example.multitenant.repository.UsersRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TestAuthHelpers {
    private final UsersRepository usersRepository;
    private final ObjectMapper objectMapper;
    private final PasswordEncoder passwordEncoder;

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

    public Cookie loginAndGetSession(String existingEmail, String existingPassword, MockMvc mockMvc, String loginUrl) throws JsonProcessingException, Exception {
        var dto = new LoginDTO();
        dto.setEmail(existingEmail);
        dto.setPassword(existingPassword);

        var result = mockMvc.perform(post(loginUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk())
            .andReturn();

        var resp = result.getResponse();
        var sessionCookie = resp.getCookie("multitenant-cookie-session");

        assertNotNull(sessionCookie, "Expected multitenant-cookie-session to be set on login");
        return sessionCookie;
    }

    public String encodePassword(String password) {
        return this.passwordEncoder.encode(password);
    }

}
