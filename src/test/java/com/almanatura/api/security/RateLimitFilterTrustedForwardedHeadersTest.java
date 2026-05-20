package com.almanatura.api.security;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.almanatura.api.entity.Project;
import com.almanatura.api.enums.ProjectPillar;
import com.almanatura.api.enums.ProjectStatus;
import com.almanatura.api.exception.ErrorCode;
import com.almanatura.api.repository.OutboundNotificationRepository;
import com.almanatura.api.repository.ProjectApplicationRepository;
import com.almanatura.api.repository.ProjectImpactEntryRepository;
import com.almanatura.api.repository.ProjectRepository;

@SpringBootTest(
        properties = {
            "app.rate-limit.trust-forwarded-headers=true",
            "app.rate-limit.register.requests=1",
            "app.rate-limit.register.window-minutes=60"
        })
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RateLimitFilterTrustedForwardedHeadersTest {

    private static final String PATH = "/applications";
    private static final String PROBLEM_JSON = MediaType.APPLICATION_PROBLEM_JSON_VALUE;

    @Autowired private MockMvc mockMvc;
    @Autowired private ProjectRepository projectRepository;
    @Autowired private ProjectApplicationRepository projectApplicationRepository;
    @Autowired private ProjectImpactEntryRepository projectImpactEntryRepository;
    @Autowired private OutboundNotificationRepository outboundNotificationRepository;

    @BeforeEach
    void setUp() {
        projectImpactEntryRepository.deleteAll();
        outboundNotificationRepository.deleteAll();
        projectApplicationRepository.deleteAll();
        projectRepository.deleteAll();
    }

    @Test
    void leftMostForwardedForDefinesTheBucket() throws Exception {
        Project published =
                projectRepository.save(
                        Project.builder()
                                .title("Published")
                                .pillar(ProjectPillar.EDUCATION)
                                .startsAt(LocalDate.parse("2030-05-01"))
                                .status(ProjectStatus.PUBLISHED)
                                .build());

        String firstBody =
                """
                {
                  "projectId": %d,
                  "fullName": "Ada Lovelace",
                  "email": "ada.one@example.org",
                  "dni": "12345678Z",
                  "phone": "+34000111222"
                }
                """
                        .formatted(published.getId());
        String secondBody =
                """
                {
                  "projectId": %d,
                  "fullName": "Grace Hopper",
                  "email": "grace.two@example.org",
                  "dni": "87654321X",
                  "phone": "+34000999888"
                }
                """
                        .formatted(published.getId());
        String thirdBody =
                """
                {
                  "projectId": %d,
                  "fullName": "Marie Curie",
                  "email": "marie.three@example.org",
                  "dni": "11111111H",
                  "phone": "+34000777666"
                }
                """
                        .formatted(published.getId());

        mockMvc.perform(
                        MockMvcRequestBuilders.post(PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Forwarded-For", "203.0.113.1, 198.51.100.2")
                                .content(firstBody))
                .andExpect(status().isCreated());

        mockMvc.perform(
                        MockMvcRequestBuilders.post(PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Forwarded-For", "203.0.113.1, 198.51.100.9")
                                .content(secondBody))
                .andExpect(status().isTooManyRequests())
                .andExpect(header().string("Retry-After", "60"))
                .andExpect(content().contentTypeCompatibleWith(PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value(ErrorCode.RATE_LIMIT_EXCEEDED.code()));

        mockMvc.perform(
                        MockMvcRequestBuilders.post(PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Forwarded-For", "198.51.100.2, 203.0.113.1")
                                .content(thirdBody))
                .andExpect(status().isCreated());
    }
}
