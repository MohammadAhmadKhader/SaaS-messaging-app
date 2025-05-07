package com.example.multitenant.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import com.example.multitenant.models.User;
import com.example.multitenant.repository.OrganizationRolesRepository;
import com.example.multitenant.repository.UsersRepository;
import com.example.multitenant.utils.BaseIntegration;
import com.example.multitenant.utils.DataLoader;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.everyItem;

import java.util.List;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestConstructor;

@RequiredArgsConstructor
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class UsersControllerTest extends BaseIntegration {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final UsersRepository usersRepository;
    private Integer defaultSearchSize = 10;
    private static final String ENDPOINT = "/api/users/search";
    private List<User> users;
    
    @BeforeAll
    private void setUp() throws Exception {
        var resource = new ClassPathResource("test-data/users.json");
        List<User> users = objectMapper.readValue(resource.getInputStream(), new TypeReference<>() {});
        var savedUsers = DataLoader.loadUsers(users, this.usersRepository, true);
        this.users = savedUsers;
    }

    @AfterAll
    private void tearDown() {
        this.usersRepository.deleteAll(this.users);
    }

    @Nested
    @DisplayName("When no filter parameters are provided")
    class NoFilters {
        @Test
        @DisplayName("Should return first page of users with default size")
        void returnsFirstPageDefaultSize() throws Exception {
            mockMvc.perform(get(ENDPOINT)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.users").isArray())
                .andExpect(jsonPath("$.users.length()").value(defaultSearchSize))
                .andExpect(jsonPath("$.hasNext").value(true))
                .andExpect(jsonPath("$.nextCursor").isNumber());
        }
    }

    @Nested
    @DisplayName("Filtering by individual fields")
    class IndividualFilters {
        @Test
        @DisplayName("Should filter by firstName")
        void filterByFirstName() throws Exception {
            mockMvc.perform(get(ENDPOINT)
                    .param("firstName", "Isabella")
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.users[*].firstName").value(everyItem(equalToIgnoringCase("Isabella"))));
        }

        @Test
        @DisplayName("Should filter by lastName")
        void filterByLastName() throws Exception {
            mockMvc.perform(get(ENDPOINT)
                    .param("lastName", "Smith")
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.users[*].lastName").value(everyItem(equalToIgnoringCase("Smith"))));
        }

        @Test
        @DisplayName("Should filter by email")
        void filterByEmail() throws Exception {
            mockMvc.perform(get(ENDPOINT)
                    .param("email", "olivia.davis8@example.com")
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.users[0].firstName").value("Olivia"))
                .andExpect(jsonPath("$.users.length()").value(1));
        }
    }

    @Nested
    @DisplayName("Filtering by multiple fields")
    class CombinedFilters {
        @Test
        @DisplayName("Should filter by firstName and lastName")
        void filterByFirstAndLastName() throws Exception {
            mockMvc.perform(get(ENDPOINT)
                    .param("firstName", "Alice")
                    .param("lastName", "Johnson")
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.users[*].firstName").value(everyItem(equalToIgnoringCase("Alice"))))
                .andExpect(jsonPath("$.users[*].lastName").value(everyItem(equalToIgnoringCase("Johnson"))));
        }
    }

    @Nested
    @DisplayName("Pagination via cursor and size")
    class CursorPagination {
        @Test
        @DisplayName("Should return page with custom size and cursor")
        void customSizeAndCursor() throws Exception {
            // retrieve initial page
            var result = mockMvc.perform(get(ENDPOINT)
                    .param("size", "2")
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

            var json = result.getResponse().getContentAsString();
            var nextCursor = JsonPath.read(json, "$.nextCursor");

            // next page with cursor
            mockMvc.perform(get(ENDPOINT)
                    .param("size", "2")
                    .param("cursorId", nextCursor.toString())
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.users").isArray());
        }
    }
}
