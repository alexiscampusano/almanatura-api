package com.almanatura.api.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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

import com.almanatura.api.entity.OutboundNotification;
import com.almanatura.api.entity.Project;
import com.almanatura.api.entity.ProjectApplication;
import com.almanatura.api.entity.ProjectImpactEntry;
import com.almanatura.api.entity.User;
import com.almanatura.api.enums.ApplicationStatus;
import com.almanatura.api.enums.NotificationChannel;
import com.almanatura.api.enums.ProjectPillar;
import com.almanatura.api.enums.ProjectStatus;
import com.almanatura.api.enums.Role;
import com.almanatura.api.exception.ErrorCode;
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
class AdminReportControllerTest {

    private static final String REPORTS_BASE = "/admin/reports";
    private static final String PROBLEM_JSON = MediaType.APPLICATION_PROBLEM_JSON_VALUE;

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private ProjectRepository projectRepository;
    @Autowired private ProjectApplicationRepository projectApplicationRepository;
    @Autowired private ActorRepository actorRepository;
    @Autowired private ProjectImpactEntryRepository projectImpactEntryRepository;
    @Autowired private OutboundNotificationRepository outboundNotificationRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private DniCipherService dniCipherService;

    private User superUser;
    private User eventManager;

    @BeforeEach
    void setUp() {
        projectImpactEntryRepository.deleteAll();
        outboundNotificationRepository.deleteAll();
        projectApplicationRepository.deleteAll();
        actorRepository.deleteAll();
        projectRepository.deleteAll();
        userRepository.deleteAll();
        superUser =
                userRepository.save(
                        User.builder()
                                .name("Super Reports")
                                .email("super.reports@almanatura.org")
                                .passwordHash(passwordEncoder.encode("SuperReports9!Z"))
                                .role(Role.SUPER_USER)
                                .enabled(true)
                                .build());
        eventManager =
                userRepository.save(
                        User.builder()
                                .name("Manager Reports")
                                .email("mgr.reports@almanatura.org")
                                .passwordHash(passwordEncoder.encode("MgrReports9!Z"))
                                .role(Role.EVENT_MANAGER)
                                .enabled(true)
                                .build());
    }

    @Test
    void summary_withoutAuth_returnsAuthenticationRequired() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REPORTS_BASE + "/summary"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value(ErrorCode.AUTHENTICATION_REQUIRED.code()));
    }

    @Test
    void projectsApplications_withoutAuth_returnsAuthenticationRequired() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REPORTS_BASE + "/projects/applications"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(ErrorCode.AUTHENTICATION_REQUIRED.code()));
    }

    @Test
    void summary_asEventManager_returnsAggregates() throws Exception {
        seedReportScenario();

        mockMvc.perform(
                        MockMvcRequestBuilders.get(REPORTS_BASE + "/summary")
                                .with(user(eventManager)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalProjects").value(4))
                .andExpect(jsonPath("$.totalApplications").value(4))
                .andExpect(jsonPath("$.projectsByStatus.length()").value(3))
                .andExpect(jsonPath("$.projectsByStatus[0].status").value("CANCELLED"))
                .andExpect(jsonPath("$.projectsByStatus[0].count").value(1))
                .andExpect(jsonPath("$.projectsByStatus[1].status").value("DRAFT"))
                .andExpect(jsonPath("$.projectsByStatus[1].count").value(1))
                .andExpect(jsonPath("$.projectsByStatus[2].status").value("PUBLISHED"))
                .andExpect(jsonPath("$.projectsByStatus[2].count").value(2))
                .andExpect(jsonPath("$.totalImpactEntries").value(0))
                .andExpect(jsonPath("$.totalOutboundNotifications").value(0));
    }

    @Test
    void summary_includesImpactAndNotificationRollups() throws Exception {
        Project p =
                projectRepository.save(
                        Project.builder()
                                .title("With planning")
                                .pillar(ProjectPillar.ENTREPRENEURSHIP)
                                .startsAt(Instant.parse("2030-08-01T10:00:00Z"))
                                .status(ProjectStatus.PUBLISHED)
                                .build());
        projectImpactEntryRepository.save(
                ProjectImpactEntry.builder()
                        .project(p)
                        .recordedAt(Instant.parse("2030-09-01T12:00:00Z"))
                        .metricLabel("Reach")
                        .build());
        outboundNotificationRepository.save(
                OutboundNotification.builder()
                        .channel(NotificationChannel.EMAIL)
                        .recipientHint("team@example.org")
                        .build());

        mockMvc.perform(
                        MockMvcRequestBuilders.get(REPORTS_BASE + "/summary")
                                .with(user(eventManager)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProjects").value(1))
                .andExpect(jsonPath("$.totalApplications").value(0))
                .andExpect(jsonPath("$.totalImpactEntries").value(1))
                .andExpect(jsonPath("$.totalOutboundNotifications").value(1));
    }

    @Test
    void projectsApplications_ordersByCountThenStartsAt() throws Exception {
        Project top =
                projectRepository.save(
                        Project.builder()
                                .title("Popular")
                                .pillar(ProjectPillar.EDUCATION)
                                .startsAt(Instant.parse("2030-06-15T10:00:00Z"))
                                .status(ProjectStatus.PUBLISHED)
                                .build());
        Project second =
                projectRepository.save(
                        Project.builder()
                                .title("Medium")
                                .pillar(ProjectPillar.HEALTH)
                                .startsAt(Instant.parse("2030-07-01T10:00:00Z"))
                                .status(ProjectStatus.PUBLISHED)
                                .build());
        Project draftEarly =
                projectRepository.save(
                        Project.builder()
                                .title("Draft early")
                                .pillar(ProjectPillar.CULTURE)
                                .startsAt(Instant.parse("2030-01-01T10:00:00Z"))
                                .status(ProjectStatus.DRAFT)
                                .build());
        Project cancelledLate =
                projectRepository.save(
                        Project.builder()
                                .title("Cancelled late")
                                .pillar(ProjectPillar.TECHNOLOGY)
                                .startsAt(Instant.parse("2030-12-01T10:00:00Z"))
                                .status(ProjectStatus.CANCELLED)
                                .build());

        for (int i = 0; i < 3; i++) {
            projectApplicationRepository.save(app(top, "u" + i + "@x.org", "1111111" + i + "H"));
        }
        projectApplicationRepository.save(app(second, "only@x.org", "22222222J"));

        mockMvc.perform(
                        MockMvcRequestBuilders.get(REPORTS_BASE + "/projects/applications")
                                .with(user(superUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0].title").value("Popular"))
                .andExpect(jsonPath("$[0].applicationCount").value(3))
                .andExpect(jsonPath("$[1].title").value("Medium"))
                .andExpect(jsonPath("$[1].applicationCount").value(1))
                .andExpect(jsonPath("$[2].title").value("Draft early"))
                .andExpect(jsonPath("$[2].applicationCount").value(0))
                .andExpect(jsonPath("$[3].title").value("Cancelled late"))
                .andExpect(jsonPath("$[3].applicationCount").value(0));
    }

    private void seedReportScenario() {
        Project draft =
                projectRepository.save(
                        Project.builder()
                                .title("Draft only")
                                .pillar(ProjectPillar.CULTURE)
                                .startsAt(Instant.parse("2030-03-01T12:00:00Z"))
                                .status(ProjectStatus.DRAFT)
                                .build());
        Project pubA =
                projectRepository.save(
                        Project.builder()
                                .title("Published A")
                                .pillar(ProjectPillar.EDUCATION)
                                .startsAt(Instant.parse("2030-04-01T12:00:00Z"))
                                .status(ProjectStatus.PUBLISHED)
                                .build());
        Project pubB =
                projectRepository.save(
                        Project.builder()
                                .title("Published B")
                                .pillar(ProjectPillar.HEALTH)
                                .startsAt(Instant.parse("2030-05-01T12:00:00Z"))
                                .status(ProjectStatus.PUBLISHED)
                                .build());
        projectRepository.save(
                Project.builder()
                        .title("Cancelled")
                        .pillar(ProjectPillar.TECHNOLOGY)
                        .startsAt(Instant.parse("2030-06-01T12:00:00Z"))
                        .status(ProjectStatus.CANCELLED)
                        .build());

        projectApplicationRepository.save(app(pubA, "a1@x.org", "11111111H"));
        projectApplicationRepository.save(app(pubA, "a2@x.org", "22222222J"));
        projectApplicationRepository.save(app(pubB, "b1@x.org", "33333333P"));
        projectApplicationRepository.save(app(draft, "draft@x.org", "44444444R"));
    }

    private ProjectApplication app(Project project, String email, String dniPlain) {
        return ProjectApplication.builder()
                .project(project)
                .status(ApplicationStatus.SUBMITTED)
                .fullName("User")
                .email(email)
                .dniEncrypted(dniCipherService.encrypt(dniPlain))
                .build();
    }
}
