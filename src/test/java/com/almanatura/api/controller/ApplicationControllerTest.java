package com.almanatura.api.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApplicationControllerTest {

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
    void submit_publishedProject_returnsCreated() throws Exception {
        Project pub =
                projectRepository.save(
                        Project.builder()
                                .title("Published")
                                .pillar(ProjectPillar.EDUCATION)
                                .startsAt(LocalDate.parse("2030-05-01"))
                                .status(ProjectStatus.PUBLISHED)
                                .build());

        mockMvc.perform(
                        MockMvcRequestBuilders.post(PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                          "projectId": %d,
                                          "fullName": "Ada Lovelace",
                                          "email": "ada@example.org",
                                          "dni": "12345678Z",
                                          "phone": "+34000111222"
                                        }
                                        """
                                                .formatted(pub.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.projectId").value(pub.getId().intValue()))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.submittedAt").exists());
    }

    @Test
    void submit_draftProject_returnsNotFound() throws Exception {
        Project draft =
                projectRepository.save(
                        Project.builder()
                                .title("Draft")
                                .pillar(ProjectPillar.HEALTH)
                                .startsAt(LocalDate.parse("2030-05-01"))
                                .status(ProjectStatus.DRAFT)
                                .build());

        mockMvc.perform(
                        MockMvcRequestBuilders.post(PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                          "projectId": %d,
                                          "fullName": "Bob",
                                          "email": "bob@example.org",
                                          "dni": "87654321X"
                                        }
                                        """
                                                .formatted(draft.getId())))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value(ErrorCode.RESOURCE_NOT_FOUND.code()));
    }

    @Test
    void submit_duplicateEmail_returnsConflict() throws Exception {
        Project pub =
                projectRepository.save(
                        Project.builder()
                                .title("Open")
                                .pillar(ProjectPillar.CULTURE)
                                .startsAt(LocalDate.parse("2030-05-01"))
                                .status(ProjectStatus.PUBLISHED)
                                .build());

        String body =
                """
                {
                  "projectId": %d,
                  "fullName": "First",
                  "email": "dup@example.org",
                  "dni": "11111111H"
                }
                """
                        .formatted(pub.getId());

        mockMvc.perform(
                        MockMvcRequestBuilders.post(PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(
                        MockMvcRequestBuilders.post(PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(ErrorCode.APPLICATION_ALREADY_EXISTS.code()));
    }
}
