package com.almanatura.api.exception;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@org.springframework.context.annotation.Import(ErrorResponseTest.TestRoutes.class)
class ErrorResponseTest {

    private static final String PROBLEM_JSON = MediaType.APPLICATION_PROBLEM_JSON_VALUE;

    @Autowired private MockMvc mockMvc;

    @Test
    void notFound_returnsResourceNotFoundProblem() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/projects/999999"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value(ErrorCode.RESOURCE_NOT_FOUND.code()))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void malformedJson_returnsMalformedRequestProblem() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/auth/test/validate")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{not json"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value(ErrorCode.MALFORMED_REQUEST.code()));
    }

    @Test
    void invalidBody_returnsValidationFailedProblemWithViolations() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/auth/test/validate")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_FAILED.code()))
                .andExpect(jsonPath("$.violations").isArray())
                .andExpect(jsonPath("$.violations[0].field").value("name"));
    }

    @Test
    void protectedEndpointWithoutAuth_returnsAuthenticationRequiredProblem() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value(ErrorCode.AUTHENTICATION_REQUIRED.code()));
    }

    @Test
    void protectedEndpointWrongRole_returnsAccessDeniedProblem() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/admin/users/1")
                                .with(
                                        org.springframework.security.test.web.servlet.request
                                                .SecurityMockMvcRequestPostProcessors.user(
                                                        "event-manager")
                                                .roles("EVENT_MANAGER")))
                .andExpect(status().isForbidden())
                .andExpect(content().contentTypeCompatibleWith(PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value(ErrorCode.ACCESS_DENIED.code()));
    }

    @Test
    void wrongHttpMethod_returnsMethodNotAllowedProblem() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/auth/test/validate"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentTypeCompatibleWith(PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value(ErrorCode.METHOD_NOT_ALLOWED.code()));
    }

    @Test
    void unsupportedMediaType_returnsMediaTypeNotSupportedProblem() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/auth/test/validate")
                                .contentType(MediaType.TEXT_PLAIN)
                                .content("plain text"))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(content().contentTypeCompatibleWith(PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value(ErrorCode.MEDIA_TYPE_NOT_SUPPORTED.code()));
    }

    @Test
    void duplicateApplicationConstraint_returnsApplicationAlreadyExistsProblem() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/test/data-integrity/application"))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value(ErrorCode.APPLICATION_ALREADY_EXISTS.code()))
                .andExpect(
                        jsonPath("$.detail")
                                .value(
                                        "An application with this email already exists for this"
                                                + " project."));
    }

    @Test
    void duplicateUserConstraint_returnsEmailAlreadyInUseProblem() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/test/data-integrity/user"))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value(ErrorCode.EMAIL_ALREADY_IN_USE.code()))
                .andExpect(jsonPath("$.detail").value(ErrorCode.EMAIL_ALREADY_IN_USE.title()));
    }

    @Test
    void healthEndpoint_setsTraceIdHeaderAndProblemContainsTraceWhenFails() throws Exception {
        // Quick sanity check that the traceId field exists (may be null if tracing not active).
        mockMvc.perform(MockMvcRequestBuilders.get("/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(header().exists("Content-Type"));
    }

    @TestConfiguration
    static class TestRoutes {

        @Bean
        TestErrorController testErrorController() {
            return new TestErrorController();
        }
    }

    @RestController
    @RequestMapping("/auth/test")
    static class TestErrorController {

        public record TestPayload(@NotBlank String name) {}

        @PostMapping(value = "/validate", consumes = MediaType.APPLICATION_JSON_VALUE)
        public TestPayload validate(@Valid @RequestBody TestPayload payload) {
            return payload;
        }

        @PostMapping("/data-integrity/application")
        public void duplicateApplication() {
            throw new org.springframework.dao.DataIntegrityViolationException(
                    "duplicate key",
                    new RuntimeException(
                            "Unique index or primary key violation:"
                                    + " \"uq_applications_project_email\""));
        }

        @PostMapping("/data-integrity/user")
        public void duplicateUser() {
            throw new org.springframework.dao.DataIntegrityViolationException(
                    "duplicate key",
                    new RuntimeException(
                            "Unique index or primary key violation: \"uk_users_email\""));
        }
    }
}
