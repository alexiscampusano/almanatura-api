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
import org.springframework.transaction.annotation.Transactional;

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
@Transactional
class ProjectControllerTest {

    private static final String PATH = "/projects";
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
    void list_returnsOnlyPublished_orderedByStartsAt() throws Exception {
        LocalDate firstStart = LocalDate.parse("2030-03-01");
        LocalDate secondStart = LocalDate.parse("2030-07-01");

        projectRepository.save(
                Project.builder()
                        .title("Draft only")
                        .pillar(ProjectPillar.CULTURE)
                        .startsAt(secondStart)
                        .status(ProjectStatus.DRAFT)
                        .build());

        projectRepository.save(
                Project.builder()
                        .title("Second public")
                        .pillar(ProjectPillar.EDUCATION)
                        .startsAt(secondStart)
                        .status(ProjectStatus.PUBLISHED)
                        .build());

        projectRepository.save(
                Project.builder()
                        .title("First public")
                        .pillar(ProjectPillar.HEALTH)
                        .startsAt(firstStart)
                        .location("North")
                        .status(ProjectStatus.PUBLISHED)
                        .build());

        mockMvc.perform(MockMvcRequestBuilders.get(PATH))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].title").value("First public"))
                .andExpect(jsonPath("$.content[1].title").value("Second public"))
                .andExpect(jsonPath("$.content[0].pillar").value("HEALTH"))
                .andExpect(jsonPath("$.content[0].status").doesNotExist());
    }

    @Test
    void list_filterByPillar() throws Exception {
        projectRepository.save(
                Project.builder()
                        .title("Tech outreach")
                        .pillar(ProjectPillar.TECHNOLOGY)
                        .startsAt(LocalDate.parse("2030-01-01"))
                        .status(ProjectStatus.PUBLISHED)
                        .build());
        projectRepository.save(
                Project.builder()
                        .title("Health outreach")
                        .pillar(ProjectPillar.HEALTH)
                        .startsAt(LocalDate.parse("2030-02-01"))
                        .status(ProjectStatus.PUBLISHED)
                        .build());

        mockMvc.perform(MockMvcRequestBuilders.get(PATH).param("pillar", "TECHNOLOGY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Tech outreach"));
    }

    @Test
    void getById_published_returnsOk() throws Exception {
        Project published =
                projectRepository.save(
                        Project.builder()
                                .title("Open call")
                                .description("Join us")
                                .pillar(ProjectPillar.ENTREPRENEURSHIP)
                                .startsAt(LocalDate.parse("2030-09-10"))
                                .endsAt(LocalDate.parse("2030-09-10"))
                                .location("Hub")
                                .status(ProjectStatus.PUBLISHED)
                                .build());

        mockMvc.perform(MockMvcRequestBuilders.get(PATH + "/" + published.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(published.getId().intValue()))
                .andExpect(jsonPath("$.title").value("Open call"))
                .andExpect(jsonPath("$.location").value("Hub"))
                .andExpect(jsonPath("$.pillar").value("ENTREPRENEURSHIP"))
                .andExpect(jsonPath("$.status").doesNotExist());
    }

    @Test
    void getById_draft_returnsNotFound() throws Exception {
        Project draft =
                projectRepository.save(
                        Project.builder()
                                .title("Internal only")
                                .pillar(ProjectPillar.CULTURE)
                                .startsAt(LocalDate.parse("2030-04-01"))
                                .status(ProjectStatus.DRAFT)
                                .build());

        mockMvc.perform(MockMvcRequestBuilders.get(PATH + "/" + draft.getId()))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value(ErrorCode.RESOURCE_NOT_FOUND.code()));
    }
}
