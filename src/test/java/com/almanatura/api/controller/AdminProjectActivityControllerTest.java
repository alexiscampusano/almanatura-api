package com.almanatura.api.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;

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

import com.almanatura.api.entity.Project;
import com.almanatura.api.entity.User;
import com.almanatura.api.enums.ProjectPillar;
import com.almanatura.api.enums.ProjectStatus;
import com.almanatura.api.enums.Role;
import com.almanatura.api.repository.ActivityParticipationRepository;
import com.almanatura.api.repository.OutboundNotificationRepository;
import com.almanatura.api.repository.ProjectActivityRepository;
import com.almanatura.api.repository.ProjectApplicationRepository;
import com.almanatura.api.repository.ProjectImpactEntryRepository;
import com.almanatura.api.repository.ProjectRepository;
import com.almanatura.api.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminProjectActivityControllerTest {

    private static final String BASE = "/admin/projects";

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private ProjectRepository projectRepository;
    @Autowired private ProjectApplicationRepository projectApplicationRepository;
    @Autowired private ActivityParticipationRepository activityParticipationRepository;
    @Autowired private ProjectActivityRepository projectActivityRepository;
    @Autowired private ProjectImpactEntryRepository projectImpactEntryRepository;
    @Autowired private OutboundNotificationRepository outboundNotificationRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private User eventManager;

    @BeforeEach
    void setUp() {
        activityParticipationRepository.deleteAll();
        projectImpactEntryRepository.deleteAll();
        outboundNotificationRepository.deleteAll();
        projectApplicationRepository.deleteAll();
        projectActivityRepository.deleteAll();
        projectRepository.deleteAll();
        userRepository.deleteAll();
        eventManager =
                userRepository.save(
                        User.builder()
                                .name("Manager")
                                .email("mgr.activities@almanatura.org")
                                .passwordHash(passwordEncoder.encode("MgrActs9!Z"))
                                .role(Role.EVENT_MANAGER)
                                .enabled(true)
                                .build());
    }

    @Test
    void createAndList_underProject_returnsScheduledByDefault() throws Exception {
        Project p =
                projectRepository.save(
                        Project.builder()
                                .title("Rural lab")
                                .pillar(ProjectPillar.EDUCATION)
                                .startsAt(Instant.parse("2030-05-01T10:00:00Z"))
                                .status(ProjectStatus.DRAFT)
                                .build());

        mockMvc.perform(
                        MockMvcRequestBuilders.post(BASE + "/" + p.getId() + "/activities")
                                .with(user(eventManager))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                          "title": "Field day",
                                          "startsAt": "2030-06-01T14:00:00Z",
                                          "location": "Meadow"
                                        }
                                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Field day"))
                .andExpect(jsonPath("$.status").value("SCHEDULED"))
                .andExpect(jsonPath("$.projectId").value(p.getId().intValue()));

        mockMvc.perform(
                        MockMvcRequestBuilders.get(BASE + "/" + p.getId() + "/activities")
                                .with(user(eventManager)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Field day"));
    }

    @Test
    void delete_unknownProject_returns404() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.delete(BASE + "/99999/activities/1")
                                .with(user(eventManager)))
                .andExpect(status().isNotFound());
    }
}
