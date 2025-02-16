package com.example.demo.controllers;

import java.util.Collections;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.session.Session;
import org.springframework.session.data.redis.ReactiveRedisIndexedSessionRepository.RedisSession;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dtos.apiResponse.ApiResponses;
import com.example.demo.dtos.auth.LoginDTO;
import com.example.demo.dtos.auth.RegisterDTO;
import com.example.demo.dtos.auth.UserPrincipal;
import com.example.demo.dtos.users.UserViewDTO;
import com.example.demo.models.User;
import com.example.demo.services.cache.RedisService;
import com.example.demo.services.security.RolesService;
import com.example.demo.services.users.UsersService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UsersService usersService;
    private final PasswordEncoder passwordEncoder;
    private final RolesService rolesService;
    private final SecurityContextRepository securityRepository = new HttpSessionSecurityContextRepository();
    private final RedisService redisService;
    
    public AuthController(AuthenticationManager authenticationManager, UsersService usersService,
     PasswordEncoder passwordEncoder, RolesService rolesService, RedisService redisService) {
       this.usersService = usersService;
       this.authenticationManager = authenticationManager;
       this.passwordEncoder = passwordEncoder;
       this.rolesService = rolesService;
       this.redisService = redisService;
    }

    @PostMapping("/register")
    public ResponseEntity<Object> register(@Valid @RequestBody RegisterDTO registerDTO, HttpServletRequest req) {
        if(usersService.existsByEmail(registerDTO.getEmail())) {
            var respBody = ApiResponses.GetErrResponse(String.format("User with email: '%s' already exists", registerDTO.getEmail()));
            return ResponseEntity.badRequest().body(respBody);
        }

        var user = registerDTO.toUser();
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        var role = rolesService.findByName("User");
        if(role == null) {
            var errMsg = "User Role was not found";
            return ResponseEntity.internalServerError().body(ApiResponses.GetInternalErr(errMsg));
        }
        user.setRoles(Collections.singletonList(role));
        
        var createdUser = usersService.create(user);
        var userDTO = createdUser.toViewDTO();

        redisService.createSessionWithUser(req, userDTO);

        var respBody = ApiResponses.OneKey("user", userDTO);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(respBody);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginDTO dto, HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
            dto.getEmail(), 
            dto.getPassword()
        ));
        
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
        securityRepository.saveContext(context, request, response);
        
        var principal = (UserPrincipal) auth.getPrincipal();
        var userViewDTO = principal.getUser().toViewDTO();
        var respBody = ApiResponses.OneKey("user", userViewDTO);
        
        redisService.storeUserInSession(request, userViewDTO);

        return ResponseEntity.ok().body(respBody);
    }

    @GetMapping("/user")
    public ResponseEntity<Object> getUserBySession(HttpServletRequest req) {
        var session = req.getSession(false);
        if(session == null) {
            var respBody = ApiResponses.Unauthorized();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(respBody);
        }

        var user = redisService.getUserFromSession(req);
        if(user == null) {
            var respBody = ApiResponses.GetInternalErr();
            return ResponseEntity.internalServerError().body(respBody);
        }
        
        return ResponseEntity.ok().body(user);
    }

    @DeleteMapping("/logout")
    public ResponseEntity<Object> logout(HttpServletRequest req) {
        var session = req.getSession(false);
        if(session == null) {
            var respBody = ApiResponses.GetErrResponse("cookie was not found");
            return ResponseEntity.badRequest().body(respBody);
        }

        session.invalidate();

        return ResponseEntity.noContent().build();
    }
}
