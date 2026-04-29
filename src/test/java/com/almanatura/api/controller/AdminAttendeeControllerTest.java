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
class AdminAttendeeControllerTest {

    private static final String EVENTS_PATH = "/admin/events";
    private static final String PROBLEM_JSON = MediaType.APPLICATION_PROBLEM_JSON_VALUE;

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private CulturalEventRepository culturalEventRepository;
    @Autowired private EventAttendeeRepository eventAttendeeRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private DniCipherService dniCipherService;

    private User superUser;

    @BeforeEach
    void setUp() {
        eventAttendeeRepository.deleteAll();
        culturalEventRepository.deleteAll();
        userRepository.deleteAll();
        superUser =
                userRepository.save(
                        User.builder()
                                .name("Super")
                                .email("super.attendees@almanatura.org")
                                .passwordHash(passwordEncoder.encode("SuperAttendees9!Z"))
                                .role(Role.SUPER_USER)
                                .enabled(true)
                                .build());
    }

    @Test
    void listAttendees_asSuperUser_returnsDecryptedDniAndOrdered() throws Exception {
        CulturalEvent event =
                culturalEventRepository.save(
                        CulturalEvent.builder()
                                .title("Taller")
                                .startsAt(Instant.parse("2030-09-01T10:00:00Z"))
                                .status(EventStatus.PUBLISHED)
                                .build());

        eventAttendeeRepository.save(
                EventAttendee.builder()
                        .culturalEvent(event)
                        .fullName("First")
                        .email("first@example.org")
                        .phone("611111111")
                        .dniEncrypted(dniCipherService.encrypt("11111111H"))
                        .build());
        eventAttendeeRepository.save(
                EventAttendee.builder()
                        .culturalEvent(event)
                        .fullName("Second")
                        .email("second@example.org")
                        .dniEncrypted(dniCipherService.encrypt("22222222J"))
                        .build());

        mockMvc.perform(
                        MockMvcRequestBuilders.get(attendeesPath(event.getId()))
                                .with(user(superUser)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].fullName").value("First"))
                .andExpect(jsonPath("$[0].email").value("first@example.org"))
                .andExpect(jsonPath("$[0].phone").value("611111111"))
                .andExpect(jsonPath("$[0].dni").value("11111111H"))
                .andExpect(jsonPath("$[0].eventId").value(event.getId().intValue()))
                .andExpect(jsonPath("$[1].fullName").value("Second"))
                .andExpect(jsonPath("$[1].dni").value("22222222J"))
                .andExpect(jsonPath("$[0].dniEncrypted").doesNotExist());
    }

    @Test
    void listAttendees_empty_returnsEmptyArray() throws Exception {
        CulturalEvent event =
                culturalEventRepository.save(
                        CulturalEvent.builder()
                                .title("Sin inscripciones")
                                .startsAt(Instant.parse("2030-10-01T12:00:00Z"))
                                .status(EventStatus.DRAFT)
                                .build());

        mockMvc.perform(
                        MockMvcRequestBuilders.get(attendeesPath(event.getId()))
                                .with(user(superUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void listAttendees_unknownEvent_returnsResourceNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(attendeesPath(999_999L)).with(user(superUser)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value(ErrorCode.RESOURCE_NOT_FOUND.code()));
    }

    @Test
    void listAttendees_withoutAuth_returnsUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(attendeesPath(1L)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value(ErrorCode.AUTHENTICATION_REQUIRED.code()));
    }

    private static String attendeesPath(long eventId) {
        return EVENTS_PATH + "/" + eventId + "/attendees";
    }
}
