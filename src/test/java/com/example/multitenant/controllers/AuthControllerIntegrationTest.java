package com.example.multitenant.controllers;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;

import com.example.multitenant.dtos.auth.LoginDTO;
import com.example.multitenant.dtos.auth.RegisterDTO;
import com.example.multitenant.dtos.auth.ResetPasswordDTO;
import com.example.multitenant.models.User;
import com.example.multitenant.testsupport.annotations.WithMockCustomUser;
import com.example.multitenant.testsupport.utils.BaseIntegrationTest;
import com.example.multitenant.testsupport.utils.TestAuthHelpers;
import com.example.multitenant.testsupport.utils.TestDbHelpers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasItem;

import java.util.HashMap;
import java.util.List;

import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.everyItem;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthControllerIntegrationTest extends BaseIntegrationTest {
    private static final String LOGIN_ENDPOINT = "/api/auth/login";
    private static final String RESET_PASSWORD_ENDPOINT = "/api/auth/reset-password";
    private static final String REGISTER_ENDPOINT = "/api/auth/register";
    private static final String USER_BY_SESSION_ENDPOINT = "/api/auth/user";
    private static final String LOGOUT_ENDPOINT = "/api/auth/logout";

    @Autowired
    private TestAuthHelpers testAuthHelpers;

    @Autowired
    private TestDbHelpers testDbHelpers;

    private String existingEmail;
    private String existingPasswordPlain = "test-password";

    private long userIdToResetPassword;
    private long userIdWithExistingPassword;
    private User userToLoginAndLogoutSuccessfully;

    @BeforeAll
    public void setUp() {
        var user = this.users.get(0);
        existingEmail = user.getEmail();
        testDbHelpers.updateUserPassword(user, existingPasswordPlain);
        this.userIdWithExistingPassword = user.getId();

        var userToResetPassword = this.users.get(2);
        testDbHelpers.updateUserPassword(userToResetPassword, existingPasswordPlain);
        this.userIdToResetPassword = userToResetPassword.getId();

        this.userToLoginAndLogoutSuccessfully = this.users.get(3);
    }

    @Nested
    @DisplayName("/register endpoint")
    class Register {
        @Test
        @DisplayName("Should register new user successfully")
        void registerSuccess() throws Exception {
            var str = "NewUser123!";
            var dto = new RegisterDTO();
            dto.setEmail("new.user@example.com");
            dto.setFirstName(str);
            dto.setLastName(str);
            dto.setPassword(str);

            mockMvc.perform(post(REGISTER_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user.email").value(dto.getEmail()));
        }

        @Test
        @DisplayName("Should return bad request for duplicate email")
        void registerDuplicateEmail() throws Exception {
            var name = "NewUser123!";
            var pass = "SomePass1!";
            var dto = new RegisterDTO();

            dto.setEmail(existingEmail);
            dto.setFirstName(name);
            dto.setLastName(name);
            dto.setPassword(pass);

            mockMvc.perform(post(REGISTER_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("User with email: '" + existingEmail + "' already exists"));
        }
    }

    @Nested
    @DisplayName("/login endpoint")
    class Login {
        @Test
        @DisplayName("Should login successfully with valid credentials")
        void loginSuccess() throws Exception {
            var dto = new LoginDTO();
            dto.setEmail(existingEmail);
            dto.setPassword(existingPasswordPlain);

            mockMvc.perform(post(LOGIN_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.email").value(existingEmail));
        }

        @Test
        @DisplayName("Should return unauthorized for invalid credentials")
        void loginInvalid() throws Exception {
            var wrongPass = "WrongPass1!";
            var dto = new LoginDTO();
            dto.setPassword(wrongPass);
            dto.setEmail(existingEmail);

            mockMvc.perform(post(LOGIN_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("/user endpoint")
    class GetUserBySession {
        @Test
        @DisplayName("Should return unauthorized if no session")
        void missingSession() throws Exception {
            mockMvc.perform(get(USER_BY_SESSION_ENDPOINT))
                .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Should return user when session is present")
        void withSession() throws Exception {
            var user = userToLoginAndLogoutSuccessfully;
            testAuthHelpers.setMockUser(user.getId(), List.of("User"), null);
            var sessionCookie = testAuthHelpers.loginAndGetSession(user.getEmail(), existingPasswordPlain, mockMvc, LOGIN_ENDPOINT);

            mockMvc.perform(get(USER_BY_SESSION_ENDPOINT).cookie(sessionCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(user.getEmail()));
        }
    }

    @Nested
    @DisplayName("/reset-password endpoint")
    class ResetPassword {
        @Test
        @DisplayName("Should return unauthorized if not authenticated")
        void unauthorized() throws Exception {
            var oldPassword = "Old1!1231";
            var newPassword = "New1!2133";
            var correctConfirmNewPassword = newPassword;

            var dto = new ResetPasswordDTO();
            
            dto.setOldPassword(oldPassword);
            dto.setNewPassword(newPassword);
            dto.setConfirmNewPassword(correctConfirmNewPassword);

            mockMvc.perform(patch(RESET_PASSWORD_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Should return bad request when new passwords do not match")
        void mismatchNewPasswords() throws Exception {
            testAuthHelpers.setMockUser(userIdWithExistingPassword, List.of("User"), null);

            var oldPassword = "Old1!3123";
            var newPassword = "New1222!";
            var wrongConfirmNewPassword = "DiffNew1213!";

            var dto = new ResetPasswordDTO();

            dto.setOldPassword(oldPassword);
            dto.setNewPassword(newPassword);
            dto.setConfirmNewPassword(wrongConfirmNewPassword);

            mockMvc.perform(patch(RESET_PASSWORD_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("new passsword and confirm new password are not equal"));
        }

        @Test
        @DisplayName("Should return bad request when old and new passwords are same")
        void oldEqualsNew() throws Exception {
            testAuthHelpers.setMockUser(userIdWithExistingPassword, List.of("User"), null);

            var dto = new ResetPasswordDTO();
            dto.setOldPassword(existingPasswordPlain);
            dto.setNewPassword(existingPasswordPlain);
            dto.setConfirmNewPassword(existingPasswordPlain);

            mockMvc.perform(patch(RESET_PASSWORD_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("old password and new password cant be the same"));
        }

        @Test
        @DisplayName("Should reset password successfully")
        void resetSuccess() throws Exception {
            testAuthHelpers.setMockUser(userIdToResetPassword, List.of("User"), null);
            var newPass = "NewPass123!";
            var dto = new ResetPasswordDTO();
            
            dto.setOldPassword(existingPasswordPlain);
            dto.setNewPassword(newPass);
            dto.setConfirmNewPassword(newPass);

            mockMvc.perform(patch(RESET_PASSWORD_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isAccepted());
        }
    }

    @Nested
    @DisplayName("/logout endpoint")
    class Logout {
        @Test
        @DisplayName("Should return bad request when no cookie found")
        void noCookie() throws Exception {
            mockMvc.perform(delete(LOGOUT_ENDPOINT))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("cookie was not found"));
        }

        @Test
        @DisplayName("Should logout successfully")
        void logoutSuccess() throws Exception {
            var sessionCookie = testAuthHelpers.loginAndGetSession(existingEmail, existingPasswordPlain, mockMvc, LOGIN_ENDPOINT);

            mockMvc.perform(delete(LOGOUT_ENDPOINT).cookie(sessionCookie))
                .andExpect(status().isNoContent());
        }
    }

    // Dto's Integration tests
    @Nested
    @DisplayName("Integration @Valid Validation")
    class IntegrationValidationTests {

        @Test
        @DisplayName("POST /register with invalid DTO should return 400 and validation messages for null fields")
        void registerNullPayload() throws Exception {
            var payload = new HashMap<String, Object>();
            payload.put("email", null);
            payload.put("firstName", null);
            payload.put("lastName", null);
            payload.put("password", null);

            mockMvc.perform(post(REGISTER_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email").value("email can not be empty"))
                .andExpect(jsonPath("$.errors.firstName").value("firstName can not be empty"))
                .andExpect(jsonPath("$.errors.lastName").value("lastName can not be empty"))
                .andExpect(jsonPath("$.errors.password").value("password can not be empty"));
        }

        @Test
        @DisplayName("POST /register with invalid DTO should return 400 and validation messages for high strings sizes")
        void registerMaxSizePayload() throws Exception {
            // exceed max lengths: email>64, firstName>64, lastName>64, password>36
            var longEmail = "e".repeat(65) + "@example.com";
            var longName = "N".repeat(65);
            var longPass = "p".repeat(37);

            var payload = new HashMap<String, Object>();
            payload.put("email", longEmail);

            payload.put("firstName", longName);
            payload.put("lastName", longName);
            payload.put("password", longPass);

            mockMvc.perform(post(REGISTER_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email").value("email must be at most 64"))
                .andExpect(jsonPath("$.errors.firstName").value("firstName must be at most 64"))
                .andExpect(jsonPath("$.errors.lastName").value("lastName must be at most 64"))
                .andExpect(jsonPath("$.errors.password").value("password must be at most 36"));
        }

        @Test
        @DisplayName("POST /register with invalid DTO should return 400 and validation messages for low strings sizes")
        void registerInvalidPayload() throws Exception {
            var payload = new HashMap<String, Object>();
            payload.put("email", "");
            payload.put("firstName", "ab");
            payload.put("lastName", "");
            payload.put("password", "123");

            mockMvc.perform(post(REGISTER_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email").value("email must be at least 6"))
                .andExpect(jsonPath("$.errors.firstName").value("firstName must be at least 3"))
                .andExpect(jsonPath("$.errors.lastName").value("lastName must be at least 3"))
                .andExpect(jsonPath("$.errors.password").value("password must be at least 6"));
        }

        @Test
        @DisplayName("POST /login with invalid DTO should return 400 and validation messages for low strings sizes")
        void loginInvalidPayload() throws Exception {
            var payload = new HashMap<String, Object>();
            payload.put("email", "x@x");
            payload.put("password", "");

            mockMvc.perform(post(LOGIN_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email").value("email must be at least 6"))
                .andExpect(jsonPath("$.errors.password").value( "password must be at least 6"));
        }

        @Test
        @DisplayName("POST /login with invalid DTO should return 400 and validation messages for null fields")
        void loginNullPayload() throws Exception {
            var payload = new HashMap<String, Object>();
            payload.put("email", null);
            payload.put("password", null);

            mockMvc.perform(post(LOGIN_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email").value("email can not be empty"))
                .andExpect(jsonPath("$.errors.password").value("password can not be empty"));
        }

        @Test
        @DisplayName("POST /login with invalid DTO should return 400 and validation messages for high strings sizes")
        void loginMaxSizePayload() throws Exception {
            var longEmail = "u".repeat(65) + "@mail.com";
            var longPass = "s".repeat(37);

            var payload = new HashMap<String, Object>();

            payload.put("email", longEmail);
            payload.put("password", longPass);

            mockMvc.perform(post(LOGIN_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email").value("email must be at most 64"))
                .andExpect(jsonPath("$.errors.password").value("password must be at most 36"));
        }


        @Test
        @DisplayName("PATCH /reset-password with invalid DTO should return 400 and validation messages for low string sizes")
        void resetPasswordInvalidPayload() throws Exception {
            var payload = new HashMap<String, Object>();
            payload.put("oldPassword", "");
            payload.put("newPassword", "short");
            payload.put("confirmNewPassword", "");

            mockMvc.perform(patch(RESET_PASSWORD_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.oldPassword").value("old password must be at least 6"))
                .andExpect(jsonPath("$.errors.newPassword").value("new password must be at least 6"))
                .andExpect(jsonPath("$.errors.confirmNewPassword").value("confirm new password must be at least 6"));
        }

        @Test
        @DisplayName("PATCH /reset-password with invalid DTO should return 400 and validation messages for null fields")
        void resetPasswordBoundaryPayload() throws Exception {
            var payload = new HashMap<String, Object>();
            payload.put("oldPassword", null);
            payload.put("newPassword", null);
            payload.put("confirmNewPassword", null);

            mockMvc.perform(patch(RESET_PASSWORD_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.oldPassword").value("old password can not be empty"))
                .andExpect(jsonPath("$.errors.newPassword").value("new password can not be empty"))
                .andExpect(jsonPath("$.errors.confirmNewPassword").value("confirm new password can not be empty"));
        }

        @Test
        @DisplayName("PATCH /reset-password with invalid DTO should return 400 and validation messages for high strings sizes")
        void resetPasswordMaxSizePayload() throws Exception {
            var longPass = "x".repeat(37);
            var payload = new HashMap<String, Object>();
            payload.put("oldPassword", longPass);
            payload.put("newPassword", longPass);
            payload.put("confirmNewPassword", longPass);

            mockMvc.perform(patch(RESET_PASSWORD_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.oldPassword").value("old password must be at most 36"))
                .andExpect(jsonPath("$.errors.newPassword").value("new password must be at most 36"))
                .andExpect(jsonPath("$.errors.confirmNewPassword").value("confirm new password must be at most 36"));
        }
    }
}