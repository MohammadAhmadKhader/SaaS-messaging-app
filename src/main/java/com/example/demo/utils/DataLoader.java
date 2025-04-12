package com.example.demo.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Example;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.models.Content;
import com.example.demo.models.GlobalPermission;
import com.example.demo.models.GlobalRole;
import com.example.demo.models.Organization;
import com.example.demo.models.OrganizationPermission;
import com.example.demo.models.OrganizationRole;
import com.example.demo.models.User;
import com.example.demo.repository.ContentsRepository;
import com.example.demo.repository.GlobalPermissionsRepository;
import com.example.demo.repository.GlobalRolesRepository;
import com.example.demo.repository.OrganizationPermissionsRepository;
import com.example.demo.repository.OrganizationRolesRepository;
import com.example.demo.repository.OrganizationsRepository;
import com.example.demo.repository.UsersRepository;
import com.example.demo.services.security.GlobalRolesService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.Getter;

@Component
public class DataLoader {

    @PersistenceContext
    private EntityManager entityManager;

    private OrganizationPermissionsRepository organizationPermissionsRepository;

    private OrganizationRolesRepository organizationRolesRepository;
    private ContentsRepository contentsRepository;

    private OrganizationsRepository organizationsRepository;
    private UsersRepository usersRepository;

    private GlobalRolesRepository globalRolesRepository;
    private GlobalPermissionsRepository globalPermissionsRepository;

    public DataLoader(OrganizationPermissionsRepository organizationPermissionsRepository, OrganizationRolesRepository organizationRolesRepository,
    ContentsRepository contentsRepository, OrganizationsRepository organizationsRepository, UsersRepository usersRepository,
    GlobalRolesRepository globalRolesRepository, GlobalPermissionsRepository globalPermissionsRepository
    ) {
        this.organizationPermissionsRepository = organizationPermissionsRepository;
        this.organizationRolesRepository = organizationRolesRepository;
        this.contentsRepository = contentsRepository;
        this.organizationsRepository =organizationsRepository;
        this.usersRepository = usersRepository;
        this.globalRolesRepository = globalRolesRepository;
        this.globalPermissionsRepository = globalPermissionsRepository;
    }

    private JsonData data;

    @Transactional
    public void loadData() throws IOException {
        var objectMapper = new ObjectMapper();
        var inputStream = new ClassPathResource("data.json").getInputStream();
        this.data = objectMapper.readValue(inputStream, JsonData.class);

        loadOrganizationPermissions();
        loadGlobalPermissions();
        loadContents();
        loadOrganizationRoles();
        loadOrganizations();
        loadUsersData();
        loadGlobalRolesData();
    }

    private void loadOrganizationPermissions() {
        if (this.organizationPermissionsRepository.count() == 0) {

            var permissions = this.data.getOrganizationPermissions();
            var permsHashMap = new HashMap<String, OrganizationPermission>();

            permissions.stream().forEach((perm) -> {
                permsHashMap.put(perm.getName(), perm);
            });

            for(var perm: permissions) {
                if(!this.organizationPermissionsRepository.findByName(perm.getName()).isPresent()) {
                    this.organizationPermissionsRepository.save(perm);
                }
            }
             
            System.out.println("permissions Loaded Successfully");
        } else {
            System.out.println("permissions already exists, skipping.");
        }
    }

    private void loadGlobalPermissions() {
        if (this.globalPermissionsRepository.count() == 0) {

            var globalPermissions = this.data.getGlobalPermissions();
            var permsHashMap = new HashMap<String, GlobalPermission>();
            
            globalPermissions.stream().forEach((perm) -> {
                permsHashMap.put(perm.getName(), perm);
            });

            for(var perm: globalPermissions) {
                if(!this.globalPermissionsRepository.findByName(perm.getName()).isPresent()) {
                    this.globalPermissionsRepository.save(perm);
                }
            }
             
            System.out.println("permissions Loaded Successfully");
        } else {
            System.out.println("permissions already exists, skipping.");
        }
    }

    private void loadOrganizationRoles() {
        if (this.organizationRolesRepository.count() == 0) {

            var roles = this.data.getOrganizationRoles();
            this.organizationRolesRepository.saveAll(roles);
            
            System.out.println("roles Loaded Successfully");
        } else {
            System.out.println("roles already exists, skipping.");
        }
    }

    private void loadContents() {
        if (contentsRepository.count() == 0) {

            var contents = this.data.getContents();
            this.contentsRepository.saveAll(contents);
            
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

    public void loadGlobalRolesData() {
        if (
            !this.globalRolesRepository.findByName("User").isPresent() ||
            !this.globalRolesRepository.findByName("Admin").isPresent()  ||
            !this.globalRolesRepository.findByName("SuperAdmin").isPresent() 
        ) {    
        
            var userRoleName = "User";
            var isFoundUserRole = this.globalRolesRepository.findByName(userRoleName).orElse(null);
            if(isFoundUserRole == null) {
                var userRole = new GlobalRole();
                userRole.setName(userRoleName);

                var userProbe = new GlobalPermission();
                userProbe.setDefaultUser(true);
                var userEx = Example.of(userProbe);
                
                var userPermissions = this.globalPermissionsRepository.findAll(userEx);
                var attachedPermissions = new ArrayList<GlobalPermission>();
                for (var permission : userPermissions) {
                    attachedPermissions.add(entityManager.merge(permission));
                }

                userRole.setPermissions(attachedPermissions);
                this.globalRolesRepository.save(userRole);

                System.out.println("User Role Loaded Successfully");
            }
            var adminRoleName = "Admin";
            var isFoundAdminRole = this.globalRolesRepository.findByName(adminRoleName).orElse(null);
            if(isFoundAdminRole == null) {
                var adminRole = new GlobalRole();
                adminRole.setName(adminRoleName);

                var adminProbe = new GlobalPermission();
                adminProbe.setDefaultAdmin(true);
                var adminEx = Example.of(adminProbe);

                var adminPermissions = this.globalPermissionsRepository.findAll(adminEx);
                var attachedPermissions = new ArrayList<GlobalPermission>();
                for (var permission : adminPermissions) {
                    attachedPermissions.add(entityManager.merge(permission));
                }

                adminRole.setPermissions(attachedPermissions);
                this.globalRolesRepository.save(adminRole);

                System.out.println("Admin Role Loaded Successfully");
            }
           
            var superAdminRoleName =  "SuperAdmin";
            var isFoundSuperAdminRole = this.globalRolesRepository.findByName(superAdminRoleName).orElse(null);
            if(isFoundSuperAdminRole == null) {
                var superAdminRole = new GlobalRole();
                superAdminRole.setName(superAdminRoleName);

                var superAdminProbe = new GlobalPermission();
                superAdminProbe.setDefaultSuperAdmin(true);
                var superAdminEx = Example.of(superAdminProbe);
                
                var superAdminPermissions = this.globalPermissionsRepository.findAll(superAdminEx);
                var attachedPermissions = new ArrayList<GlobalPermission>();
                for (var permission : superAdminPermissions) {
                    attachedPermissions.add(entityManager.merge(permission));
                }

                superAdminRole.setPermissions(attachedPermissions);
                this.globalRolesRepository.save(superAdminRole);

                System.out.println("SuperAdmin Role Loaded Successfully");
            }
        } else {
            System.out.println("users_roles already exists, skipping.");
        }
    }
}

@Getter
class JsonData {
    List<OrganizationPermission> organizationPermissions;
    List<OrganizationRole> organizationRoles;
    List<GlobalRole> globalRoles;
    List<GlobalPermission> globalPermissions;
    List<Content> contents;
    List<Organization> organizations;
    List<User> users;
}

@Component
class DataLoaderInitCaller {

    @Autowired
    ApplicationContext applicationContext;

    @Autowired 
    DataLoader dataLoader;

    @PostConstruct
    public void Init() {
        try {
            dataLoader.loadData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}