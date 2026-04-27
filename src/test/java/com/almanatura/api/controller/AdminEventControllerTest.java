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

import com.almanatura.api.entity.User;
import com.almanatura.api.enums.Role;
import com.almanatura.api.exception.ErrorCode;
import com.almanatura.api.repository.CulturalEventRepository;
import com.almanatura.api.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminEventControllerTest {

    private static final String EVENTS_PATH = "/admin/events";
    private static final String PROBLEM_JSON = MediaType.APPLICATION_PROBLEM_JSON_VALUE;

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private CulturalEventRepository culturalEventRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private User superUser;
    private User eventManager;

    @BeforeEach
    void setUp() {
        culturalEventRepository.deleteAll();
        userRepository.deleteAll();
        superUser =
                userRepository.save(
                        User.builder()
                                .name("Super")
                                .email("super.events@almanatura.org")
                                .passwordHash(passwordEncoder.encode("SuperEvents9!Z"))
                                .role(Role.SUPER_USER)
                                .enabled(true)
                                .build());
        eventManager =
                userRepository.save(
                        User.builder()
                                .name("Manager")
                                .email("mgr.events@almanatura.org")
                                .passwordHash(passwordEncoder.encode("MgrEvents9!Z"))
                                .role(Role.EVENT_MANAGER)
                                .enabled(true)
                                .build());
    }

    @Test
    void list_withoutAuth_returnsAuthenticationRequired() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(EVENTS_PATH))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value(ErrorCode.AUTHENTICATION_REQUIRED.code()));
    }

    @Test
    void create_withoutAuth_returnsAuthenticationRequired() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post(EVENTS_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(minimalEventJson()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(ErrorCode.AUTHENTICATION_REQUIRED.code()));
    }

    @Test
    void create_asEventManager_returnsCreated() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post(EVENTS_PATH)
                                .with(user(eventManager))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(fullEventJson()))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Taller de cerámica"))
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andExpect(jsonPath("$.id").isNumber());
    }

    @Test
    void create_asSuperUser_returnsCreated() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post(EVENTS_PATH)
                                .with(user(superUser))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(minimalEventJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Minimal"))
                .andExpect(jsonPath("$.status").value("DRAFT"));
    }

    @Test
    void list_and_getById_asSuperUser_returnOk() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post(EVENTS_PATH)
                                .with(user(superUser))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(fullEventJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(org.hamcrest.Matchers.notNullValue()));

        mockMvc.perform(MockMvcRequestBuilders.get(EVENTS_PATH).with(user(superUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Taller de cerámica"));

        long id = culturalEventRepository.findAllByOrderByStartsAtAsc().getFirst().getId();
        mockMvc.perform(MockMvcRequestBuilders.get(EVENTS_PATH + "/" + id).with(user(superUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value((int) id))
                .andExpect(jsonPath("$.location").value("Sala A"));
    }

    @Test
    void getById_unknown_returnsNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(EVENTS_PATH + "/999").with(user(superUser)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value(ErrorCode.RESOURCE_NOT_FOUND.code()));
    }

    @Test
    void create_blankTitle_returnsValidationFailed() throws Exception {
        String body =
                "{\"title\":\"   \",\"startsAt\":\""
                        + Instant.parse("2030-06-01T15:00:00Z").toString()
                        + "\"}";
        mockMvc.perform(
                        MockMvcRequestBuilders.post(EVENTS_PATH)
                                .with(user(superUser))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_FAILED.code()));
    }

    @Test
    void create_endBeforeStart_returnsValidationFailed() throws Exception {
        Instant start = Instant.parse("2030-06-10T18:00:00Z");
        Instant end = Instant.parse("2030-06-10T12:00:00Z");
        String body =
                "{\"title\":\"Bad\",\"startsAt\":\"" + start + "\",\"endsAt\":\"" + end + "\"}";
        mockMvc.perform(
                        MockMvcRequestBuilders.post(EVENTS_PATH)
                                .with(user(superUser))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_FAILED.code()));
    }

    private static String minimalEventJson() {
        return "{\"title\":\"Minimal\",\"startsAt\":\""
                + Instant.parse("2030-05-01T10:00:00Z")
                + "\"}";
    }

    private static String fullEventJson() {
        return "{"
                + "\"title\":\"Taller de cerámica\","
                + "\"description\":\"Para mayores\","
                + "\"startsAt\":\"2030-07-15T14:30:00Z\","
                + "\"endsAt\":\"2030-07-15T16:30:00Z\","
                + "\"location\":\"Sala A\","
                + "\"maxAttendees\":20"
                + "}";
    }
}
