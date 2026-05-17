package com.almanatura.api.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

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
import com.almanatura.api.repository.OutboundNotificationRepository;
import com.almanatura.api.repository.ProjectApplicationRepository;
import com.almanatura.api.repository.ProjectImpactEntryRepository;
import com.almanatura.api.repository.ProjectRepository;
import com.almanatura.api.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminProjectImpactControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private ProjectRepository projectRepository;
    @Autowired private ProjectApplicationRepository projectApplicationRepository;
    @Autowired private ProjectImpactEntryRepository projectImpactEntryRepository;
    @Autowired private OutboundNotificationRepository outboundNotificationRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private User eventManager;

    @BeforeEach
    void setUp() {
        projectImpactEntryRepository.deleteAll();
        outboundNotificationRepository.deleteAll();
        projectApplicationRepository.deleteAll();
        projectRepository.deleteAll();
        userRepository.deleteAll();
        eventManager =
                userRepository.save(
                        User.builder()
                                .name("Mgr")
                                .email("mgr.impact@almanatura.org")
                                .passwordHash(passwordEncoder.encode("MgrImpact9!Z"))
                                .role(Role.EVENT_MANAGER)
                                .enabled(true)
                                .build());
    }

    @Test
    void listAndCreate() throws Exception {
        Project p =
                projectRepository.save(
                        Project.builder()
                                .title("Impact project")
                                .pillar(ProjectPillar.TECHNOLOGY)
                                .startsAt(LocalDate.parse("2030-01-01"))
                                .status(ProjectStatus.PUBLISHED)
                                .build());

        String base = "/admin/projects/" + p.getId() + "/impact-entries";

        mockMvc.perform(MockMvcRequestBuilders.get(base).with(user(eventManager)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        mockMvc.perform(
                        MockMvcRequestBuilders.post(base)
                                .with(user(eventManager))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                          "recordedAt": "2030-12-01T12:00:00Z",
                                          "metricLabel": "Participants trained",
                                          "numericValue": 42,
                                          "notes": "Pilot cohort"
                                        }
                                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.metricLabel").value("Participants trained"))
                .andExpect(jsonPath("$.numericValue").value(42));

        mockMvc.perform(MockMvcRequestBuilders.get(base).with(user(eventManager)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}
