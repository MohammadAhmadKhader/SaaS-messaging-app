package com.example.demo.utils;

import java.io.IOException;
import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.demo.models.Content;
import com.example.demo.models.Organization;
import com.example.demo.models.Permission;
import com.example.demo.models.Role;
import com.example.demo.models.User;
import com.example.demo.repository.contents.ContentsRepository;
import com.example.demo.repository.organizations.OrganizationsRepository;
import com.example.demo.repository.permissions.PermissionsRepository;
import com.example.demo.repository.roles.RolesRepository;
import com.example.demo.repository.users.UsersRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;

@Component
public class DataLoader implements ApplicationRunner {

    private PermissionsRepository permissionsRepository;
    private RolesRepository rolesRepository;
    private ContentsRepository contentsRepository;
    private OrganizationsRepository organizationsRepository;
    private UsersRepository usersRepository;

    public DataLoader(PermissionsRepository permissionsRepository, RolesRepository rolesRepository,
    ContentsRepository contentsRepository, OrganizationsRepository organizationsRepository, UsersRepository usersRepository) {
        this.permissionsRepository =permissionsRepository;
        this.rolesRepository = rolesRepository;
        this.contentsRepository = contentsRepository;
        this.organizationsRepository =organizationsRepository;
        this.usersRepository = usersRepository;
    }

    private JsonData data;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        loadData();
    }

    public void loadData() throws IOException {
        var objectMapper = new ObjectMapper();
        var inputStream = new ClassPathResource("data.json").getInputStream();
        this.data = objectMapper.readValue(inputStream, JsonData.class);

        loadPermissions();
        loadContents();
        loadRoles();
        loadOrganizations();
        loadUsersData();
        // loadUsersRolesData();
        // loadPermissionsRolesData();
    }

    private void loadPermissions() {
        if (permissionsRepository.count() == 0) {

            var permissions = this.data.getPermissions();
            permissionsRepository.saveAll(permissions);
            
            System.out.println("permissions Loaded Successfully");
        } else {
            System.out.println("permissions already exists, skipping.");
        }
    }

    private void loadRoles() {
        if (rolesRepository.count() == 0) {

            var roles = this.data.getRoles();
            rolesRepository.saveAll(roles);
            
            System.out.println("roles Loaded Successfully");
        } else {
            System.out.println("roles already exists, skipping.");
        }
    }

    private void loadContents() {
        if (contentsRepository.count() == 0) {

            var contents = this.data.getContents();
            contentsRepository.saveAll(contents);
            
            System.out.println("contents Loaded Successfully");
        } else {
            System.out.println("contents already exists, skipping.");
        }
    }

    private void loadOrganizations() {
        if (this.organizationsRepository.count() == 0) {
            var orgs = this.data.getOrganizations();
            this.organizationsRepository.saveAll(orgs);
            
            System.out.println("organizations Loaded Successfully");
        } else {
            System.out.println("organizations already exists, skipping.");
        }
    }

    private void loadUsersData() {
        if (this.usersRepository.count() == 0) {
            var users = this.data.getUsers();
            var passwordEncoder = new BCryptPasswordEncoder();
        
            users.forEach(user -> {
                user.setPassword("123456");
                var hashedPassword = passwordEncoder.encode(user.getPassword());
                user.setPassword(hashedPassword);
            });
        
            this.usersRepository.saveAll(users);
            System.out.println("users Loaded Successfully");
        } else {
            System.out.println("users already exists, skipping.");
        }
    }

    // private void loadUsersRolesData() {
    //     if (this.usersRolesRepository.count() == 0) {
           
    //         var usersRoles = this.data.getUsers_roles();
    //         this.usersRolesRepository.saveAll(usersRoles);
    //         System.out.println("users_roles Loaded Successfully");
    //     } else {
    //         System.out.println("users_roles already exists, skipping.");
    //     }
    // }

    // private void loadPermissionsRolesData() {
    //     if (this.rolesPermissionsRepository.count() == 0) {
    //         var rolesPermissions = this.data.getPermissions_roles();
    //         this.rolesPermissionsRepository.saveAll(rolesPermissions);
            
    //         System.out.println("roles_permissions Loaded Successfully");
    //     } else {
    //         System.out.println("roles_permissions already exists, skipping.");
    //     }
    // }
}

@Getter
class JsonData {
    List<Permission> permissions;
    List<Role> roles;
    List<Content> contents;
    List<Organization> organizations;
    List<User> users;
}