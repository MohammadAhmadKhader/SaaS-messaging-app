package com.example.multitenant.controllers;

import java.util.List;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;

import com.example.multitenant.models.Organization;
import com.example.multitenant.models.User;
import com.example.multitenant.repository.OrgsRepository;
import com.example.multitenant.repository.UsersRepository;
import com.example.multitenant.utils.BaseIntegration;
import com.example.multitenant.utils.DataLoader;
import com.example.multitenant.utils.FakeDataGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.everyItem;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class OrganizationsControllerTest extends BaseIntegration {
    private final MockMvc mockMvc;
    private final OrgsRepository organizationsRepository;
    private final UsersRepository usersRepository;
    private final ObjectMapper objectMapper;
    private List<Organization> organizations;
    private List<User> owners;

    private static final String ENDPOINT = "/api/organizations/search";

    @BeforeAll
    void setUp() throws Exception {
        var resource = new ClassPathResource("test-data/organizations.json");
        var organizations = objectMapper.readValue(resource.getInputStream(), new TypeReference<List<Organization>>() {});

        var orgs = DataLoader.loadTestOrganizations(organizations, this.usersRepository, this.organizationsRepository);
        this.organizations = orgs;
        
        var orgOwners = this.organizations.stream().map((org) -> org.getOwner()).toList();
        this.owners = orgOwners;
    }

    @AfterAll
    void tearDown() throws Exception {
        this.organizationsRepository.deleteAll(this.organizations);
        this.usersRepository.deleteAll(this.owners);
    }

    @Nested
    @DisplayName("No filter parameters")
    class NoFilters {
        @Test
        @DisplayName("Should return first page of organizations with default size")
        void returnsDefaultPage() throws Exception {
            mockMvc.perform(get(ENDPOINT)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.organizations").isArray())
                .andExpect(jsonPath("$.hasNext").exists())
                .andExpect(jsonPath("$.nextCursor").exists());
        }
    }

    @Nested
    @DisplayName("Filtering by name")
    class NameFilter {
        @Test
        @DisplayName("Should return organizations filtered by name")
        void filterByName() throws Exception {
            mockMvc.perform(get(ENDPOINT)
                    .param("name", "Nebula Nexus")
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.organizations[*].name").value(everyItem(containsStringIgnoringCase("Nebula Nexus"))));
        }
    }

    @Nested
    @DisplayName("Cursor Pagination")
    class CursorPagination {
        @Test
        @DisplayName("Should paginate with custom size and cursor")
        void paginate() throws Exception {
            var result = mockMvc.perform(get(ENDPOINT)
                    .param("size", "2")
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

            var json = result.getResponse().getContentAsString();
            var nextCursor = JsonPath.read(json, "$.nextCursor");

            mockMvc.perform(get(ENDPOINT)
                    .param("cursorId", nextCursor.toString())
                    .param("size", "2")
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.organizations").isArray());
        }
    }
}
