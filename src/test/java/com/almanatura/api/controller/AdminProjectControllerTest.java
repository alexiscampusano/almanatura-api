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
import com.almanatura.api.exception.ErrorCode;
import com.almanatura.api.repository.OutboundNotificationRepository;
import com.almanatura.api.repository.ProjectApplicationRepository;
import com.almanatura.api.repository.ProjectImpactEntryRepository;
import com.almanatura.api.repository.ProjectRepository;
import com.almanatura.api.repository.UserRepository;
import com.almanatura.api.util.DniCipherService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminProjectControllerTest {

    private static final String ADMIN_PROJECTS = "/admin/projects";

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private ProjectRepository projectRepository;
    @Autowired private ProjectApplicationRepository projectApplicationRepository;
    @Autowired private ProjectImpactEntryRepository projectImpactEntryRepository;
    @Autowired private OutboundNotificationRepository outboundNotificationRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private DniCipherService dniCipherService;

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
                                .name("Manager")
                                .email("mgr.projects@almanatura.org")
                                .passwordHash(passwordEncoder.encode("MgrProjects9!Z"))
                                .role(Role.EVENT_MANAGER)
                                .enabled(true)
                                .build());
    }

    @Test
    void create_asEventManager_returnsDraft() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post(ADMIN_PROJECTS)
                                .with(user(eventManager))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                          "title": "Beekeeping workshop",
                                          "description": "Rural skills",
                                          "pillar": "TECHNOLOGY",
                                          "startsAt": "2030-04-01",
                                          "location": "Village hall"
                                        }
                                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Beekeeping workshop"))
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andExpect(jsonPath("$.pillar").value("TECHNOLOGY"));
    }

    @Test
    void list_asEventManager_returnsAllProjects() throws Exception {
        projectRepository.save(
                Project.builder()
                        .title("Alpha")
                        .pillar(ProjectPillar.TECHNOLOGY)
                        .startsAt(LocalDate.parse("2030-01-01"))
                        .status(ProjectStatus.DRAFT)
                        .build());
        projectRepository.save(
                Project.builder()
                        .title("Beta")
                        .pillar(ProjectPillar.HEALTH)
                        .startsAt(LocalDate.parse("2030-02-01"))
                        .status(ProjectStatus.PUBLISHED)
                        .build());

        mockMvc.perform(
                        MockMvcRequestBuilders.get(ADMIN_PROJECTS)
                                .with(user(eventManager)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Alpha"))
                .andExpect(jsonPath("$[1].title").value("Beta"));
    }

    @Test
    void getById_existingProject_returnsProject() throws Exception {
        Project p =
                projectRepository.save(
                        Project.builder()
                                .title("Found me")
                                .pillar(ProjectPillar.EDUCATION)
                                .startsAt(LocalDate.parse("2030-03-01"))
                                .status(ProjectStatus.DRAFT)
                                .build());

        mockMvc.perform(
                        MockMvcRequestBuilders.get(ADMIN_PROJECTS + "/" + p.getId())
                                .with(user(eventManager)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Found me"))
                .andExpect(jsonPath("$.status").value("DRAFT"));
    }

    @Test
    void getById_notFound_returnsResourceNotFound() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get(ADMIN_PROJECTS + "/99999")
                                .with(user(eventManager)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ErrorCode.RESOURCE_NOT_FOUND.code()));
    }

    @Test
    void update_asEventManager_updatesProject() throws Exception {
        Project p =
                projectRepository.save(
                        Project.builder()
                                .title("Original")
                                .pillar(ProjectPillar.CULTURE)
                                .startsAt(LocalDate.parse("2030-04-01"))
                                .status(ProjectStatus.DRAFT)
                                .build());

        mockMvc.perform(
                        MockMvcRequestBuilders.put(ADMIN_PROJECTS + "/" + p.getId())
                                .with(user(eventManager))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                          "title": "Updated Title",
                                          "description": "Updated description",
                                          "pillar": "EDUCATION",
                                          "status": "PUBLISHED",
                                          "startsAt": "2030-05-01",
                                          "location": "New location"
                                        }
                                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.pillar").value("EDUCATION"));
    }

    @Test
    void delete_withApplications_returnsConflict() throws Exception {
        Project p =
                projectRepository.save(
                        Project.builder()
                                .title("Has applicants")
                                .pillar(ProjectPillar.HEALTH)
                                .startsAt(LocalDate.parse("2030-06-01"))
                                .status(ProjectStatus.PUBLISHED)
                                .build());
        projectApplicationRepository.save(
                ProjectApplication.builder()
                        .project(p)
                        .status(ApplicationStatus.SUBMITTED)
                        .fullName("Applicant")
                        .email("applicant@example.org")
                        .dniEncrypted(dniCipherService.encrypt("12345678Z"))
                        .build());

        mockMvc.perform(
                        MockMvcRequestBuilders.delete(ADMIN_PROJECTS + "/" + p.getId())
                                .with(user(eventManager)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(ErrorCode.PROJECT_HAS_APPLICATIONS.code()));
    }
}
