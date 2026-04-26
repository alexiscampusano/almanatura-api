package com.almanatura.api.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.almanatura.api.entity.User;
import com.almanatura.api.enums.Role;
import com.almanatura.api.exception.ErrorCode;
import com.almanatura.api.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminUserControllerTest {

    private static final String USERS_PATH = "/admin/users";
    private static final String PROBLEM_JSON = MediaType.APPLICATION_PROBLEM_JSON_VALUE;

    private static final String SUPER_EMAIL = "super.list@almanatura.org";
    private static final String SUPER_NAME = "Super Lister";
    private static final String SUPER_PASSWORD = "ListUsers9!Strong";

    private static final String MANAGER_EMAIL = "manager.list@almanatura.org";
    private static final String MANAGER_PASSWORD = "ManagerList9!X";

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private User superUser;
    private User eventManager;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        superUser =
                userRepository.save(
                        User.builder()
                                .name(SUPER_NAME)
                                .email(SUPER_EMAIL)
                                .passwordHash(passwordEncoder.encode(SUPER_PASSWORD))
                                .role(Role.SUPER_USER)
                                .enabled(true)
                                .build());
        eventManager =
                userRepository.save(
                        User.builder()
                                .name("Eve Manager")
                                .email(MANAGER_EMAIL)
                                .passwordHash(passwordEncoder.encode(MANAGER_PASSWORD))
                                .role(Role.EVENT_MANAGER)
                                .enabled(true)
                                .build());
    }

    @Test
    void list_withoutAuth_returnsAuthenticationRequired() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(USERS_PATH))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value(ErrorCode.AUTHENTICATION_REQUIRED.code()));
    }

    @Test
    void create_withoutAuth_returnsAuthenticationRequired() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post(USERS_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        createUserJson(
                                                "X",
                                                "x@almanatura.org",
                                                "NewUserPass9!Z",
                                                "EVENT_MANAGER",
                                                null)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(ErrorCode.AUTHENTICATION_REQUIRED.code()));
    }

    @Test
    void list_asEventManager_returnsAccessDenied() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(USERS_PATH).with(user(eventManager)))
                .andExpect(status().isForbidden())
                .andExpect(content().contentTypeCompatibleWith(PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value(ErrorCode.ACCESS_DENIED.code()));
    }

    @Test
    void create_asEventManager_returnsAccessDenied() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post(USERS_PATH)
                                .with(user(eventManager))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        createUserJson(
                                                "X",
                                                "x@almanatura.org",
                                                "NewUserPass9!Z",
                                                "EVENT_MANAGER",
                                                null)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(ErrorCode.ACCESS_DENIED.code()));
    }

    @Test
    void create_asSuperUser_returnsCreatedAndUserSummary() throws Exception {
        String email = "new.internal@almanatura.org";
        mockMvc.perform(
                        MockMvcRequestBuilders.post(USERS_PATH)
                                .with(user(superUser))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        createUserJson(
                                                "New Internal",
                                                email,
                                                "BrandNew9!Pass",
                                                "EVENT_MANAGER",
                                                true)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.name").value("New Internal"))
                .andExpect(jsonPath("$.role").value("EVENT_MANAGER"))
                .andExpect(jsonPath("$.id").isNumber());
    }

    @Test
    void list_asSuperUser_returnsAllSummariesOrdered() throws Exception {
        String email = "another@almanatura.org";
        mockMvc.perform(
                        MockMvcRequestBuilders.post(USERS_PATH)
                                .with(user(superUser))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        createUserJson(
                                                "Another User",
                                                email,
                                                "Another9!Pass",
                                                "EVENT_MANAGER",
                                                null)))
                .andExpect(status().isCreated());

        mockMvc.perform(MockMvcRequestBuilders.get(USERS_PATH).with(user(superUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").value(superUser.getId().intValue()))
                .andExpect(jsonPath("$[1].id").value(eventManager.getId().intValue()))
                .andExpect(jsonPath("$[2].email").value(email));
    }

    @Test
    void create_duplicateEmail_returnsConflict() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post(USERS_PATH)
                                .with(user(superUser))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        createUserJson(
                                                "Clone",
                                                SUPER_EMAIL,
                                                "SomeOther9!Pwd",
                                                "EVENT_MANAGER",
                                                null)))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value(ErrorCode.EMAIL_ALREADY_IN_USE.code()));
    }

    @Test
    void create_weakPassword_returnsValidationFailed() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post(USERS_PATH)
                                .with(user(superUser))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        createUserJson(
                                                "Weak",
                                                "weak.pwd@almanatura.org",
                                                "short",
                                                "EVENT_MANAGER",
                                                null)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_FAILED.code()))
                .andExpect(
                        jsonPath("$.violations[*].field")
                                .value(org.hamcrest.Matchers.hasItem("password")));
    }

    private static String createUserJson(
            String name, String email, String password, String role, Boolean enabled) {
        String enabledPart = enabled == null ? "" : ",\"enabled\":" + enabled;
        return "{\"name\":\""
                + name
                + "\",\"email\":\""
                + email
                + "\",\"password\":\""
                + password
                + "\",\"role\":\""
                + role
                + "\""
                + enabledPart
                + "}";
    }
}
