package com.example.multitenant.testsupport.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.springframework.core.env.Environment;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.example.multitenant.models.*;
import com.example.multitenant.repository.*;
import com.example.multitenant.repository.logsrepositories.KickLogsRepository;
import com.example.multitenant.repository.logsrepositories.MemberShipsLogsRepository;
import com.example.multitenant.services.membership.MemberShipService;
import com.example.multitenant.services.security.GlobalRolesService;
import com.example.multitenant.utils.dataloader.DataLoader;
import com.example.multitenant.utils.dataloader.JsonData;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Transactional
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BaseIntegrationTest extends BaseTests {
    protected String tenantHeaderName = "X-Tenant-ID";
    @Autowired
    private LettuceConnectionFactory connectionFactory;
    
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected DataLoaderServce dataLoader;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private MemberShipService memberShipService;

    @Autowired
    private MembershipRepository membershipRepository;

    @Autowired
    private OrgsRepository organizationsRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired 
    private GlobalPermissionsRepository globalPermissionsRepository;

    @Autowired
    private GlobalRolesRepository globalRolesRepository;

    @Autowired
    private OrgPermissionsRepository orgPermissionsRepository;

    @Autowired
    private OrgRolesRepository orgRolesRepository;

    @Autowired
    private KickLogsRepository kickLogsRepository;

    @Autowired
    private MemberShipsLogsRepository memberShipsLogsRepository; 

    @Autowired
    private DataLoader dataSeeder;

    @Autowired
    private Environment environment;

    protected List<Organization> organizations;
    protected List<User> owners;
    protected List<User> users;
    protected List<Membership> memberships = new ArrayList<Membership>();

    @BeforeAll
    public void baseIntegrationSetUp() throws Exception {
        var activeProfiles = Arrays.asList(environment.getActiveProfiles());
        if (activeProfiles.contains("dev") || activeProfiles.contains("prod")) {
            var errMsg = String.format("invalid profile set, expected 'test' or 'local-test' but got: %s", activeProfiles);
            throw new RuntimeException(errMsg);
        }
        this.clearData();

        var rolesJson = new ClassPathResource("seeding-data.json");
        var seedingData = this.objectMapper.readValue(rolesJson.getInputStream(), new TypeReference<JsonData>() {});
        var globalPermissions = seedingData.getGlobalPermissions();
        this.globalPermissionsRepository.saveAll(globalPermissions);

        var orgPermissions = seedingData.getOrganizationPermissions();
        this.orgPermissionsRepository.saveAll(orgPermissions);

        // seeding global roles with their relations with permissions
        dataSeeder.loadGlobalRolesData();

        var orgsJson = new ClassPathResource("test-data/organizations.json");
        var organizations = this.objectMapper.readValue(orgsJson.getInputStream(), new TypeReference<List<Organization>>() {});

        var usersJson = new ClassPathResource("test-data/users.json");
        var users = this.objectMapper.readValue(usersJson.getInputStream(), new TypeReference<List<User>>() {});

        var orgs = this.dataLoader.loadTestOrganizations(organizations, memberships);
        var savedUsers = this.dataLoader.loadUsers(users);
        this.users = savedUsers;

        this.organizations = orgs;
        
        var orgOwners = this.organizations.stream().map((org) -> org.getOwner()).toList();
        this.owners = orgOwners;
    }

    @AfterAll
    public void tearDown() throws Exception {
        this.clearData();
    }

    public void clearData() {
        var globalRoles = this.globalRolesRepository.findAll();
        for (var globalRole : globalRoles) {
            globalRole.setPermissions(new ArrayList<>());
        }
        this.globalRolesRepository.saveAll(globalRoles);

        this.globalRolesRepository.deleteAll();
        this.globalPermissionsRepository.deleteAll();
        var orgs = this.organizationsRepository.findAll();
        for (var org : orgs) {
            org.setOwner(null);
        }

        this.kickLogsRepository.deleteAll();
        this.memberShipsLogsRepository.deleteAll();
        this.organizationsRepository.saveAll(orgs);
        this.organizationsRepository.deleteAll();
        this.orgPermissionsRepository.deleteAll();
        this.usersRepository.deleteAll();

        try (RedisConnection conn = connectionFactory.getConnection()) {
            conn.serverCommands().flushDb();
        }
    }
}