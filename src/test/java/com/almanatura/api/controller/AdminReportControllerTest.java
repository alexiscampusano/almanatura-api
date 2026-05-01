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

import com.almanatura.api.entity.CulturalEvent;
import com.almanatura.api.entity.EventAttendee;
import com.almanatura.api.entity.User;
import com.almanatura.api.enums.EventStatus;
import com.almanatura.api.enums.Role;
import com.almanatura.api.exception.ErrorCode;
import com.almanatura.api.repository.CulturalEventRepository;
import com.almanatura.api.repository.EventAttendeeRepository;
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
    @Autowired private CulturalEventRepository culturalEventRepository;
    @Autowired private EventAttendeeRepository eventAttendeeRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private DniCipherService dniCipherService;

    private User superUser;
    private User eventManager;

    @BeforeEach
    void setUp() {
        eventAttendeeRepository.deleteAll();
        culturalEventRepository.deleteAll();
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
    void attendance_withoutAuth_returnsAuthenticationRequired() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REPORTS_BASE + "/events/attendance"))
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
                .andExpect(jsonPath("$.totalEvents").value(4))
                .andExpect(jsonPath("$.totalRegistrations").value(4))
                .andExpect(jsonPath("$.eventsByStatus.length()").value(3))
                .andExpect(jsonPath("$.eventsByStatus[0].status").value("CANCELLED"))
                .andExpect(jsonPath("$.eventsByStatus[0].count").value(1))
                .andExpect(jsonPath("$.eventsByStatus[1].status").value("DRAFT"))
                .andExpect(jsonPath("$.eventsByStatus[1].count").value(1))
                .andExpect(jsonPath("$.eventsByStatus[2].status").value("PUBLISHED"))
                .andExpect(jsonPath("$.eventsByStatus[2].count").value(2));
    }

    @Test
    void attendance_asSuperUser_ordersByCountThenStartsAt() throws Exception {
        CulturalEvent top =
                culturalEventRepository.save(
                        CulturalEvent.builder()
                                .title("Popular")
                                .startsAt(Instant.parse("2030-06-15T10:00:00Z"))
                                .status(EventStatus.PUBLISHED)
                                .build());
        CulturalEvent second =
                culturalEventRepository.save(
                        CulturalEvent.builder()
                                .title("Medium")
                                .startsAt(Instant.parse("2030-07-01T10:00:00Z"))
                                .status(EventStatus.PUBLISHED)
                                .build());
        CulturalEvent draftEarly =
                culturalEventRepository.save(
                        CulturalEvent.builder()
                                .title("Draft early")
                                .startsAt(Instant.parse("2030-01-01T10:00:00Z"))
                                .status(EventStatus.DRAFT)
                                .build());
        CulturalEvent cancelledLate =
                culturalEventRepository.save(
                        CulturalEvent.builder()
                                .title("Cancelled late")
                                .startsAt(Instant.parse("2030-12-01T10:00:00Z"))
                                .status(EventStatus.CANCELLED)
                                .build());

        for (int i = 0; i < 3; i++) {
            eventAttendeeRepository.save(attendee(top, "u" + i + "@x.org", "1111111" + i + "H"));
        }
        eventAttendeeRepository.save(attendee(second, "only@x.org", "22222222J"));

        mockMvc.perform(
                        MockMvcRequestBuilders.get(REPORTS_BASE + "/events/attendance")
                                .with(user(superUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0].title").value("Popular"))
                .andExpect(jsonPath("$[0].attendeeCount").value(3))
                .andExpect(jsonPath("$[1].title").value("Medium"))
                .andExpect(jsonPath("$[1].attendeeCount").value(1))
                .andExpect(jsonPath("$[2].title").value("Draft early"))
                .andExpect(jsonPath("$[2].attendeeCount").value(0))
                .andExpect(jsonPath("$[3].title").value("Cancelled late"))
                .andExpect(jsonPath("$[3].attendeeCount").value(0));
    }

    private void seedReportScenario() {
        CulturalEvent draft =
                culturalEventRepository.save(
                        CulturalEvent.builder()
                                .title("Draft only")
                                .startsAt(Instant.parse("2030-03-01T12:00:00Z"))
                                .status(EventStatus.DRAFT)
                                .build());
        CulturalEvent pubA =
                culturalEventRepository.save(
                        CulturalEvent.builder()
                                .title("Published A")
                                .startsAt(Instant.parse("2030-04-01T12:00:00Z"))
                                .status(EventStatus.PUBLISHED)
                                .build());
        CulturalEvent pubB =
                culturalEventRepository.save(
                        CulturalEvent.builder()
                                .title("Published B")
                                .startsAt(Instant.parse("2030-05-01T12:00:00Z"))
                                .status(EventStatus.PUBLISHED)
                                .build());
        culturalEventRepository.save(
                CulturalEvent.builder()
                        .title("Cancelled")
                        .startsAt(Instant.parse("2030-06-01T12:00:00Z"))
                        .status(EventStatus.CANCELLED)
                        .build());

        eventAttendeeRepository.save(attendee(pubA, "a1@x.org", "11111111H"));
        eventAttendeeRepository.save(attendee(pubA, "a2@x.org", "22222222J"));
        eventAttendeeRepository.save(attendee(pubB, "b1@x.org", "33333333P"));
        eventAttendeeRepository.save(attendee(draft, "draft@x.org", "44444444R"));
    }

    private EventAttendee attendee(CulturalEvent event, String email, String dniPlain) {
        return EventAttendee.builder()
                .culturalEvent(event)
                .fullName("User")
                .email(email)
                .dniEncrypted(dniCipherService.encrypt(dniPlain))
                .build();
    }
}
