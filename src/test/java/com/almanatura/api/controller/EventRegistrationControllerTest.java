package com.almanatura.api.controller;

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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.almanatura.api.entity.CulturalEvent;
import com.almanatura.api.enums.EventStatus;
import com.almanatura.api.exception.ErrorCode;
import com.almanatura.api.repository.CulturalEventRepository;
import com.almanatura.api.repository.EventAttendeeRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EventRegistrationControllerTest {

    private static final MediaType JSON = MediaType.APPLICATION_JSON;
    private static final String PROBLEM_JSON = MediaType.APPLICATION_PROBLEM_JSON_VALUE;

    @Autowired private MockMvc mockMvc;
    @Autowired private CulturalEventRepository culturalEventRepository;
    @Autowired private EventAttendeeRepository eventAttendeeRepository;

    @BeforeEach
    void setUp() {
        eventAttendeeRepository.deleteAll();
        culturalEventRepository.deleteAll();
    }

    @Test
    void register_publishedEvent_returnsCreated() throws Exception {
        CulturalEvent published = savePublishedWithCapacity(25);

        mockMvc.perform(
                        MockMvcRequestBuilders.post(path(published.getId()))
                                .contentType(JSON)
                                .content(registerJson("ana.garcia@example.org")))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(JSON))
                .andExpect(jsonPath("$.eventId").value(published.getId().intValue()))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.registeredAt").exists());
    }

    @Test
    void register_draftEvent_returnsNotFound() throws Exception {
        CulturalEvent draft =
                culturalEventRepository.save(
                        CulturalEvent.builder()
                                .title("Borrador")
                                .startsAt(Instant.parse("2030-06-01T10:00:00Z"))
                                .status(EventStatus.DRAFT)
                                .build());

        mockMvc.perform(
                        MockMvcRequestBuilders.post(path(draft.getId()))
                                .contentType(JSON)
                                .content(registerJson("x@y.org")))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value(ErrorCode.RESOURCE_NOT_FOUND.code()));
    }

    @Test
    void register_unknownEvent_returnsNotFound() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/events/999999/register")
                                .contentType(JSON)
                                .content(registerJson("x@y.org")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ErrorCode.RESOURCE_NOT_FOUND.code()));
    }

    @Test
    void register_duplicateEmail_returnsConflict() throws Exception {
        CulturalEvent published = savePublishedWithCapacity(50);
        String email = "dup@example.org";

        mockMvc.perform(
                        MockMvcRequestBuilders.post(path(published.getId()))
                                .contentType(JSON)
                                .content(registerJson(email)))
                .andExpect(status().isCreated());

        mockMvc.perform(
                        MockMvcRequestBuilders.post(path(published.getId()))
                                .contentType(JSON)
                                .content(registerJson(email)))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value(ErrorCode.ATTENDEE_ALREADY_REGISTERED.code()));
    }

    @Test
    void register_capacityFull_returnsConflict() throws Exception {
        CulturalEvent published = savePublishedWithCapacity(1);

        mockMvc.perform(
                        MockMvcRequestBuilders.post(path(published.getId()))
                                .contentType(JSON)
                                .content(registerJson("first@example.org")))
                .andExpect(status().isCreated());

        mockMvc.perform(
                        MockMvcRequestBuilders.post(path(published.getId()))
                                .contentType(JSON)
                                .content(registerJson("second@example.org")))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value(ErrorCode.EVENT_AT_CAPACITY.code()));
    }

    @Test
    void register_invalidEmail_returnsValidationFailed() throws Exception {
        CulturalEvent published = savePublishedWithCapacity(10);

        mockMvc.perform(
                        MockMvcRequestBuilders.post(path(published.getId()))
                                .contentType(JSON)
                                .content(
                                        "{\"fullName\":\"Ana\",\"email\":\"not-email\",\"dni\":\"12345678A\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_FAILED.code()));
    }

    private static String path(long eventId) {
        return "/events/" + eventId + "/register";
    }

    private static String registerJson(String email) {
        return "{\"fullName\":\"Ana García\",\"email\":\""
                + email
                + "\",\"dni\":\"12345678A\",\"phone\":\"600111222\"}";
    }

    private CulturalEvent savePublishedWithCapacity(int maxAttendees) {
        return culturalEventRepository.save(
                CulturalEvent.builder()
                        .title("Taller público")
                        .startsAt(Instant.parse("2030-08-01T16:00:00Z"))
                        .status(EventStatus.PUBLISHED)
                        .maxAttendees(maxAttendees)
                        .build());
    }
}
