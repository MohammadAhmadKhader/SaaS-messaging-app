package com.example.multitenant.controllers;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.event.annotation.BeforeTestMethod;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.example.multitenant.dtos.organizations.OrgCreateDTO;
import com.example.multitenant.models.*;
import com.example.multitenant.services.membership.MemberShipService;
import com.example.multitenant.services.security.GlobalPermissions;
import com.example.multitenant.services.security.OrgPermissions;
import com.example.multitenant.testsupport.annotations.WithMockCustomUser;
import com.example.multitenant.testsupport.utils.BaseIntegrationTest;
import com.example.multitenant.testsupport.utils.TestAuthHelpers;
import com.example.multitenant.testsupport.utils.TestDbHelpers;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.stripe.model.Event.Data;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.everyItem;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import lombok.RequiredArgsConstructor;

@SpringBootTest
@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@TestInstance(TestInstance.Lifecycle.PER_CLASS) 
public class OrgsControllerIntegrationTest extends BaseIntegrationTest {
    private static final String ENDPOINT = "/api/organizations";
    private final TestDbHelpers testDbHelpers;
    private final TestAuthHelpers testAuthHelpers;
    private static int orgIdToRemoveUserFrom;
    private static int orgIdToLeave;

    private static long userIdToBeRemovedFromOrg;
    private static long userIdPerformingKickAction;

    private static long userIdToLeaveOrg;

    @BeforeAll
    public void setUp() {
        var userToRemoveFromOrg = this.users.get(0);
        var userToLeaveOrg = this.users.get(1);
        
        var orgToPerformKicking = this.organizations.get(0);
        var orgToLeaving = this.organizations.get(5);

        orgIdToRemoveUserFrom = orgToPerformKicking.getId();
        orgIdToLeave = orgToLeaving.getId();
        
        userIdToBeRemovedFromOrg = userToRemoveFromOrg.getId();
        userIdToLeaveOrg = userToLeaveOrg.getId();
    
        userIdPerformingKickAction = orgToPerformKicking.getOwner().getId();
        
        this.testDbHelpers.addUserToOrganization(userIdToBeRemovedFromOrg, orgIdToRemoveUserFrom);
        this.testDbHelpers.addUserToOrganization(userIdToLeaveOrg, orgIdToLeave);
    }

    @Nested
    @DisplayName("No filter parameters")
    class NoFilters {
        @Test
        @DisplayName("Should return first page of organizations with default size")
        void returnsDefaultPage() throws Exception {
            mockMvc.perform(get(ENDPOINT + "/search")
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
            mockMvc.perform(get(ENDPOINT + "/search")
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
            var result = mockMvc.perform(get(ENDPOINT + "/search")
                    .param("size", "2")
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

            var json = result.getResponse().getContentAsString();
            var nextCursor = JsonPath.read(json, "$.nextCursor");

            mockMvc.perform(get(ENDPOINT + "/search")
                    .param("cursorId", nextCursor.toString())
                    .param("size", "2")
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.organizations").isArray());
        }
    }
    
    @Test
    @WithMockCustomUser(username = "jane.smith2@gmail.com", roles = {"User"}, authorities ={GlobalPermissions.ORG_CREATE})
    @DisplayName("Should create organization")
    void createOrganization() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/organizations")
                .param("name", "organization name")
                .param("industry", "industry")
                .with(request -> {
                    request.setMethod("POST");
                    return request;
                }))
            .andExpect(status().isCreated());
    }
    
    @Test
    @WithMockCustomUser(username = "jane.smith2@gmail.com", authorities = {GlobalPermissions.ORG_CREATE})
    @DisplayName("Should return error when organization name is taken")
    void createOrganizationWithNameTaken() throws Exception {
        var existingOrg = this.organizations.get(0);

        var dto = new OrgCreateDTO(existingOrg.getName(), existingOrg.getIndustry(), null);
        this.mockMvc.perform(MockMvcRequestBuilders.multipart("/api/organizations")
            .param("name", dto.name())
            .param("industry", dto.industry())
            .with(request -> {
                request.setMethod("POST");
                return request;
            }))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("organization name was taken already"));
    }

    @Test
    @WithMockCustomUser(username = "jane.smith2@gmail.com")
    @DisplayName("Should return forbidden response for not having the Organization create authority")
    void createOrganizationWithNoAuthority() throws Exception {
        var dto = new OrgCreateDTO("organization name 101", "industry", null);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/organizations")
            .param("name", dto.name())
            .param("industry", dto.industry())
            .with(request -> {
                request.setMethod("POST");
                return request;
            }))
            .andExpect(status().isForbidden())
            .andReturn();
    }

    @Test
    @DisplayName("Should return unauthorized response for not being a signed user")
    void createOrganizationWithNoUser() throws Exception {
        var dto = new OrgCreateDTO("organization name 100", "industry", null);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/organizations")
            .param("name", dto.name())
            .param("industry", dto.industry())
            .with(request -> {
                request.setMethod("POST");
                return request;
            }))
        .andExpect(status().isUnauthorized());
    }  

    @Test
    @DisplayName("Should kick user from organization")
    public void kickUser() throws Exception {
        testAuthHelpers.setMockUser(userIdPerformingKickAction, null, List.of(OrgPermissions.USER_KICK));

        this.mockMvc.perform(patch("/api/organizations/memberships/" + userIdToBeRemovedFromOrg + "/kick")
                .contentType(MediaType.APPLICATION_JSON)
                .header(tenantHeaderName, orgIdToRemoveUserFrom))
            .andExpect(status().isAccepted());
    }
    
    @Test
    @WithMockCustomUser(username = "jane.smith2@gmail.com", roles = {"User","Owner","Admin"}, authorities ={OrgPermissions.USER_KICK})
    @DisplayName("Should return error when user is not part of the organization")
    void kickUserNotPartOfOrganization() throws Exception {
        var firstOrgIndex = 1;
        var secondOrgIndex = 2;
        var firstOrg = this.organizations.get(firstOrgIndex);
        var secondOrg = this.organizations.get(secondOrgIndex);
    
        // attempting to kick the owner of second org from the first org (he is not part of the first org)
        this.mockMvc.perform(patch(ENDPOINT + "/memberships/" + secondOrg.getOwner().getId() + "/kick")
                .contentType(MediaType.APPLICATION_JSON)
                .header(tenantHeaderName, firstOrg.getId()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error")
            .value("user is not part of the organization"));
    }
    
    @Test
    @DisplayName("Should leave organization")
    void leaveOrganization() throws Exception {
        testAuthHelpers.setMockUser(userIdToLeaveOrg, null, null);

        this.mockMvc.perform(patch("/api/organizations/leave/" + orgIdToLeave)
                .contentType(MediaType.APPLICATION_JSON)
                .header(tenantHeaderName, orgIdToLeave))
            .andExpect(status().isAccepted());
    }
    
    @Test
    @WithMockCustomUser(username = "sarah.wilson6@gmail.com")
    @DisplayName("Should return error when user is owner of the organization")
    void leaveOrganizationAsOwner() throws Exception {
        var org = this.organizations.get(5);
        var orgId = org.getId();
    
        this.mockMvc.perform(patch("/api/organizations/leave/" + orgId)
                .contentType(MediaType.APPLICATION_JSON)
                .header(tenantHeaderName, orgId))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error")
            .value("can not leave an owned organization you have to delete it, or transfer ownership first"));
    }
}