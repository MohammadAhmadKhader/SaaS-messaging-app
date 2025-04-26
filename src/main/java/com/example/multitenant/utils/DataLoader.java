package com.example.multitenant.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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

import com.example.multitenant.models.*;
import com.example.multitenant.models.enums.*;
import com.example.multitenant.repository.*;
import com.example.multitenant.services.security.GlobalRolesService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
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
        assignGlobalRoles();
    }

    @Transactional
    private void loadOrganizationPermissions() {
        var permissions = this.data.getOrganizationPermissions();
        var permsHashMap = new HashMap<String, OrganizationPermission>();

        permissions.stream().forEach((perm) -> {
            permsHashMap.put(perm.getName(), perm);
        });

        var ownerPerms = new ArrayList<OrganizationPermission>();
        var adminPerms = new ArrayList<OrganizationPermission>();
        var userPerms = new ArrayList<OrganizationPermission>();
        for(var perm: permissions) {
            if(!this.organizationPermissionsRepository.findByName(perm.getName()).isPresent()) {
                this.organizationPermissionsRepository.save(perm);
                if(perm.getIsDefaultOrgOwner()) {
                    ownerPerms.add(perm);
                }

                if(perm.getIsDefaultAdmin()) {
                    adminPerms.add(perm);
                }

                if(perm.getIsDefaultUser()) {
                    userPerms.add(perm);
                }
            }
        }

        if(ownerPerms.size() > 0) {
            DataLoaderHelper.addNewOrganizationPermissions
            (organizationRolesRepository, ownerPerms, DefaultOrganizationRole.ORG_OWNER);
        }

        if(adminPerms.size() > 0) {
            DataLoaderHelper.addNewOrganizationPermissions
            (organizationRolesRepository, adminPerms, DefaultOrganizationRole.ORG_ADMIN);
        }

        if(userPerms.size() > 0) {
            DataLoaderHelper.addNewOrganizationPermissions
            (organizationRolesRepository, userPerms, DefaultOrganizationRole.ORG_USER);
        }  
             
        System.out.println("permissions were checked");
    }

    private void loadGlobalPermissions() {
        var globalPermissions = this.data.getGlobalPermissions();

        for(var perm: globalPermissions) {
            if(!this.globalPermissionsRepository.findByName(perm.getName()).isPresent()) {
                this.globalPermissionsRepository.save(perm);
            }
        }
             
        System.out.println("permissions Loaded Successfully");
    }

    private void assignGlobalRoles() {
        var globalRolesJson = this.data.getGlobalRoles();
        var globalPermsJson = this.data.getGlobalPermissions();
        
        var defaultGlobalRoles = new ArrayList<String>(List.of(DefaultGlobalRole.USER.getRoleName(), DefaultGlobalRole.ADMIN.getRoleName(), DefaultGlobalRole.SUPERADMIN.getRoleName()));
        for (var defaultGlobalRole : defaultGlobalRoles) {
            for(var globalRoleJson: globalRolesJson) {
                var optRole = this.globalRolesRepository.findByName(globalRoleJson.getName());

                if(optRole.isPresent()) {
                    var role = optRole.get();

                    if(role.getName().equals(defaultGlobalRole)) {
                        for (var globalPermJson : globalPermsJson) {
                            var hasPermission = role.getPermissions().stream().
                                                    anyMatch((perm) -> perm.getName().equals(globalPermJson.getName()));

                            if(defaultGlobalRole.equals("User") && globalPermJson.getIsDefaultUser() && !hasPermission) {
                                role.getPermissions().add(globalPermJson);
                                this.globalRolesRepository.save(role);

                            } else if(defaultGlobalRole.equals("Admin") && globalPermJson.getIsDefaultAdmin() && !hasPermission) {
                                role.getPermissions().add(globalPermJson);
                                this.globalRolesRepository.save(role);

                            } else if (defaultGlobalRole.equals("SuperAdmin") && globalPermJson.getIsDefaultSuperAdmin() && !hasPermission) {
                                role.getPermissions().add(globalPermJson);
                                this.globalRolesRepository.save(role);

                            }
                        }
                    }
                }
            }
        }

        System.out.println("Global roles were loaded successfully");
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
            !this.globalRolesRepository.findByName(DefaultGlobalRole.USER.getRoleName()).isPresent() ||
            !this.globalRolesRepository.findByName(DefaultGlobalRole.ADMIN.getRoleName()).isPresent()  ||
            !this.globalRolesRepository.findByName(DefaultGlobalRole.SUPERADMIN.getRoleName()).isPresent() 
        ) {    
        
            var userRoleName = DefaultGlobalRole.USER.getRoleName();
            var isFoundUserRole = this.globalRolesRepository.findByName(userRoleName).orElse(null);
            if(isFoundUserRole == null) {
                var userRole = new GlobalRole();
                userRole.setName(userRoleName);

                var userProbe = new GlobalPermission();
                userProbe.setIsDefaultUser(true);
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
            var adminRoleName = DefaultGlobalRole.ADMIN.getRoleName();
            var isFoundAdminRole = this.globalRolesRepository.findByName(adminRoleName).orElse(null);
            if(isFoundAdminRole == null) {
                var adminRole = new GlobalRole();
                adminRole.setName(adminRoleName);

                var adminProbe = new GlobalPermission();
                adminProbe.setIsDefaultAdmin(true);
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
           
            var superAdminRoleName =  DefaultGlobalRole.SUPERADMIN.getRoleName();
            var isFoundSuperAdminRole = this.globalRolesRepository.findByName(superAdminRoleName).orElse(null);
            if(isFoundSuperAdminRole == null) {
                var superAdminRole = new GlobalRole();
                superAdminRole.setName(superAdminRoleName);

                var superAdminProbe = new GlobalPermission();
                superAdminProbe.setIsDefaultSuperAdmin(true);
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
            System.out.println("loading global roles was skipped.");
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

class DataLoaderHelper {
    static void addNewOrganizationPermissions(OrganizationRolesRepository repo, 
    List<OrganizationPermission> newPerms, DefaultOrganizationRole role) {
        var probe = new OrganizationRole();
        probe.setName(role.getRoleName());

        var ownerRoles = repo.findAll(Example.of(probe));

        for (OrganizationRole organizationRole : ownerRoles) {
           organizationRole.getOrganizationPermissions().addAll(newPerms);
           repo.save(organizationRole);
        }
    }
}