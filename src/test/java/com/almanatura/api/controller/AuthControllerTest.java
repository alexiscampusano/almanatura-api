package com.almanatura.api.controller;

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
class AuthControllerTest {

    private static final String LOGIN_PATH = "/auth/login";
    private static final String PROBLEM_JSON = MediaType.APPLICATION_PROBLEM_JSON_VALUE;

    private static final String ENABLED_EMAIL = "ana.admin@almanatura.org";
    private static final String ENABLED_NAME = "Ana Admin";
    private static final String ENABLED_PASSWORD = "Sup3rSecret!";

    private static final String DISABLED_EMAIL = "old.manager@almanatura.org";
    private static final String DISABLED_PASSWORD = "Sup3rSecret!";

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        userRepository.save(
                User.builder()
                        .name(ENABLED_NAME)
                        .email(ENABLED_EMAIL)
                        .passwordHash(passwordEncoder.encode(ENABLED_PASSWORD))
                        .role(Role.SUPER_USER)
                        .enabled(true)
                        .build());
        userRepository.save(
                User.builder()
                        .name("Old Manager")
                        .email(DISABLED_EMAIL)
                        .passwordHash(passwordEncoder.encode(DISABLED_PASSWORD))
                        .role(Role.EVENT_MANAGER)
                        .enabled(false)
                        .build());
    }

    @Test
    void validCredentials_returnBearerTokenAndUserProjection() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post(LOGIN_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(loginBody(ENABLED_EMAIL, ENABLED_PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").isString())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").isNumber())
                .andExpect(jsonPath("$.user.email").value(ENABLED_EMAIL))
                .andExpect(jsonPath("$.user.name").value(ENABLED_NAME))
                .andExpect(jsonPath("$.user.role").value(Role.SUPER_USER.name()))
                .andExpect(jsonPath("$.user.id").isNumber())
                .andExpect(jsonPath("$.user.passwordHash").doesNotExist())
                .andExpect(jsonPath("$.user.password").doesNotExist());
    }

    @Test
    void wrongPassword_returnsInvalidCredentialsProblem() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post(LOGIN_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(loginBody(ENABLED_EMAIL, "not-the-password")))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_CREDENTIALS.code()))
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void unknownEmail_returnsInvalidCredentialsProblemNotResourceNotFound() throws Exception {
        // Same code as wrong password: never disclose whether the email exists.
        mockMvc.perform(
                        MockMvcRequestBuilders.post(LOGIN_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(loginBody("ghost@almanatura.org", ENABLED_PASSWORD)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_CREDENTIALS.code()));
    }

    @Test
    void disabledAccount_returnsAccountDisabledProblem() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post(LOGIN_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(loginBody(DISABLED_EMAIL, DISABLED_PASSWORD)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value(ErrorCode.ACCOUNT_DISABLED.code()));
    }

    @Test
    void missingEmail_returnsValidationFailedProblem() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post(LOGIN_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"password\":\"Sup3rSecret!\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_FAILED.code()))
                .andExpect(
                        jsonPath("$.violations[*].field")
                                .value(org.hamcrest.Matchers.hasItem("email")));
    }

    @Test
    void malformedEmail_returnsValidationFailedProblem() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post(LOGIN_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(loginBody("not-an-email", ENABLED_PASSWORD)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_FAILED.code()))
                .andExpect(
                        jsonPath("$.violations[*].field")
                                .value(org.hamcrest.Matchers.hasItem("email")));
    }

    @Test
    void missingPassword_returnsValidationFailedProblem() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post(LOGIN_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"email\":\"" + ENABLED_EMAIL + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_FAILED.code()))
                .andExpect(
                        jsonPath("$.violations[*].field")
                                .value(org.hamcrest.Matchers.hasItem("password")));
    }

    @Test
    void unsupportedMediaType_returnsMediaTypeProblem() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post(LOGIN_PATH)
                                .contentType(MediaType.TEXT_PLAIN)
                                .content("plain text"))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(content().contentTypeCompatibleWith(PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value(ErrorCode.MEDIA_TYPE_NOT_SUPPORTED.code()));
    }

    @Test
    void wrongHttpMethod_returnsMethodNotAllowedProblem() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(LOGIN_PATH))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentTypeCompatibleWith(PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value(ErrorCode.METHOD_NOT_ALLOWED.code()));
    }

    private static String loginBody(String email, String password) {
        return "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";
    }
}
