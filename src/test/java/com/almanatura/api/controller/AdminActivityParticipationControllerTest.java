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

import com.almanatura.api.entity.Actor;
import com.almanatura.api.entity.Project;
import com.almanatura.api.entity.ProjectActivity;
import com.almanatura.api.entity.User;
import com.almanatura.api.enums.ProjectActivityStatus;
import com.almanatura.api.enums.ProjectPillar;
import com.almanatura.api.enums.ProjectStatus;
import com.almanatura.api.enums.Role;
import com.almanatura.api.exception.ErrorCode;
import com.almanatura.api.repository.ActivityParticipationRepository;
import com.almanatura.api.repository.ActorRepository;
import com.almanatura.api.repository.OutboundNotificationRepository;
import com.almanatura.api.repository.ProjectActivityRepository;
import com.almanatura.api.repository.ProjectApplicationRepository;
import com.almanatura.api.repository.ProjectImpactEntryRepository;
import com.almanatura.api.repository.ProjectRepository;
import com.almanatura.api.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminActivityParticipationControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private ProjectRepository projectRepository;
    @Autowired private ProjectApplicationRepository projectApplicationRepository;
    @Autowired private ActivityParticipationRepository activityParticipationRepository;
    @Autowired private ProjectActivityRepository projectActivityRepository;
    @Autowired private ProjectImpactEntryRepository projectImpactEntryRepository;
    @Autowired private OutboundNotificationRepository outboundNotificationRepository;
    @Autowired private ActorRepository actorRepository;
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
        actorRepository.deleteAll();
        userRepository.deleteAll();
        eventManager =
                userRepository.save(
                        User.builder()
                                .name("Mgr")
                                .email("mgr.part@almanatura.org")
                                .passwordHash(passwordEncoder.encode("MgrPart9!Z"))
                                .role(Role.EVENT_MANAGER)
                                .enabled(true)
                                .build());
    }

    @Test
    void inviteAndPatchStatus() throws Exception {
        Project p =
                projectRepository.save(
                        Project.builder()
                                .title("P")
                                .pillar(ProjectPillar.HEALTH)
                                .startsAt(Instant.parse("2030-01-01T10:00:00Z"))
                                .status(ProjectStatus.PUBLISHED)
                                .build());
        ProjectActivity act =
                projectActivityRepository.save(
                        ProjectActivity.builder()
                                .project(p)
                                .title("Workshop")
                                .startsAt(Instant.parse("2030-02-01T10:00:00Z"))
                                .status(ProjectActivityStatus.SCHEDULED)
                                .build());
        Actor actor =
                actorRepository.save(
                        Actor.builder().fullName("Rural lead").region("North").build());

        String base =
                "/admin/projects/" + p.getId() + "/activities/" + act.getId() + "/participations";

        mockMvc.perform(
                        MockMvcRequestBuilders.post(base)
                                .with(user(eventManager))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        { "actorId": %d }
                                        """
                                                .formatted(actor.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("INVITED"))
                .andExpect(jsonPath("$.actorId").value(actor.getId().intValue()));

        long partId =
                activityParticipationRepository
                        .findByActivity_IdOrderByIdAsc(act.getId())
                        .getFirst()
                        .getId();

        mockMvc.perform(
                        MockMvcRequestBuilders.patch(base + "/" + partId)
                                .with(user(eventManager))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        { "status": "CONFIRMED" }
                                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void inviteDuplicate_returnsConflict() throws Exception {
        Project p =
                projectRepository.save(
                        Project.builder()
                                .title("P2")
                                .pillar(ProjectPillar.CULTURE)
                                .startsAt(Instant.parse("2030-01-01T10:00:00Z"))
                                .status(ProjectStatus.PUBLISHED)
                                .build());
        ProjectActivity act =
                projectActivityRepository.save(
                        ProjectActivity.builder()
                                .project(p)
                                .title("Meet")
                                .startsAt(Instant.parse("2030-02-01T10:00:00Z"))
                                .status(ProjectActivityStatus.SCHEDULED)
                                .build());
        Actor actor = actorRepository.save(Actor.builder().fullName("A").build());

        String base =
                "/admin/projects/" + p.getId() + "/activities/" + act.getId() + "/participations";
        String body =
                """
                { "actorId": %d }
                """
                        .formatted(actor.getId());

        mockMvc.perform(
                        MockMvcRequestBuilders.post(base)
                                .with(user(eventManager))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(
                        MockMvcRequestBuilders.post(base)
                                .with(user(eventManager))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(ErrorCode.PARTICIPATION_ALREADY_EXISTS.code()));
    }
}
