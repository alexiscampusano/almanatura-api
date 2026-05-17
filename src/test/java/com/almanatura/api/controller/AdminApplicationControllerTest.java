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
import com.almanatura.api.entity.ProjectApplication;
import com.almanatura.api.entity.User;
import com.almanatura.api.enums.ApplicationStatus;
import com.almanatura.api.enums.ProjectPillar;
import com.almanatura.api.enums.ProjectStatus;
import com.almanatura.api.enums.Role;
import com.almanatura.api.repository.ActorRepository;
import com.almanatura.api.repository.OutboundNotificationRepository;
import com.almanatura.api.repository.ProjectApplicationRepository;
import com.almanatura.api.repository.ProjectImpactEntryRepository;
import com.almanatura.api.repository.ProjectRepository;
import com.almanatura.api.repository.UserRepository;
import com.almanatura.api.util.DniCipherService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminApplicationControllerTest {

    private static final String BASE = "/admin/applications";

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private ProjectRepository projectRepository;
    @Autowired private ProjectApplicationRepository projectApplicationRepository;
    @Autowired private ProjectImpactEntryRepository projectImpactEntryRepository;
    @Autowired private OutboundNotificationRepository outboundNotificationRepository;
    @Autowired private ActorRepository actorRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private DniCipherService dniCipherService;

    private User eventManager;

    @BeforeEach
    void setUp() {
        projectImpactEntryRepository.deleteAll();
        outboundNotificationRepository.deleteAll();
        projectApplicationRepository.deleteAll();
        actorRepository.deleteAll();
        projectRepository.deleteAll();
        userRepository.deleteAll();
        eventManager =
                userRepository.save(
                        User.builder()
                                .name("Reviewer")
                                .email("reviewer@almanatura.org")
                                .passwordHash(passwordEncoder.encode("Reviewer9!Z"))
                                .role(Role.EVENT_MANAGER)
                                .enabled(true)
                                .build());
    }

    @Test
    void patch_approvedToRegistered_createsActor() throws Exception {
        Project project =
                projectRepository.save(
                        Project.builder()
                                .title("Incubator")
                                .pillar(ProjectPillar.ENTREPRENEURSHIP)
                                .startsAt(LocalDate.parse("2030-01-01"))
                                .status(ProjectStatus.PUBLISHED)
                                .build());
        ProjectApplication app =
                projectApplicationRepository.save(
                        ProjectApplication.builder()
                                .project(project)
                                .status(ApplicationStatus.APPROVED)
                                .fullName("Founder One")
                                .email("founder@example.org")
                                .dniEncrypted(dniCipherService.encrypt("12345678Z"))
                                .build());

        mockMvc.perform(
                        MockMvcRequestBuilders.patch(BASE + "/" + app.getId())
                                .with(user(eventManager))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"status\":\"REGISTERED_AS_ACTOR\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REGISTERED_AS_ACTOR"))
                .andExpect(jsonPath("$.actorId").isNumber())
                .andExpect(jsonPath("$.nationalId").value("12345678Z"));
    }

    @Test
    void patch_invalidTransition_returnsBadRequest() throws Exception {
        Project project =
                projectRepository.save(
                        Project.builder()
                                .title("P")
                                .pillar(ProjectPillar.CULTURE)
                                .startsAt(LocalDate.parse("2030-01-01"))
                                .status(ProjectStatus.PUBLISHED)
                                .build());
        ProjectApplication app =
                projectApplicationRepository.save(
                        ProjectApplication.builder()
                                .project(project)
                                .status(ApplicationStatus.SUBMITTED)
                                .fullName("A")
                                .email("a@example.org")
                                .dniEncrypted(dniCipherService.encrypt("87654321X"))
                                .build());

        mockMvc.perform(
                        MockMvcRequestBuilders.patch(BASE + "/" + app.getId())
                                .with(user(eventManager))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"status\":\"REGISTERED_AS_ACTOR\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_APPLICATION_TRANSITION"));
    }
}
