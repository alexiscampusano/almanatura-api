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
class EventControllerTest {

    private static final String EVENTS_PATH = "/events";
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
    void list_returnsOnlyPublished_orderedByStartsAt() throws Exception {
        Instant firstStart = Instant.parse("2030-03-01T10:00:00Z");
        Instant secondStart = Instant.parse("2030-07-01T10:00:00Z");

        culturalEventRepository.save(
                CulturalEvent.builder()
                        .title("Borrador")
                        .description("Oculto")
                        .startsAt(secondStart)
                        .status(EventStatus.DRAFT)
                        .build());

        culturalEventRepository.save(
                CulturalEvent.builder()
                        .title("Segundo")
                        .startsAt(secondStart)
                        .location("Sala B")
                        .status(EventStatus.PUBLISHED)
                        .build());

        culturalEventRepository.save(
                CulturalEvent.builder()
                        .title("Primero")
                        .startsAt(firstStart)
                        .location("Sala A")
                        .status(EventStatus.PUBLISHED)
                        .build());

        mockMvc.perform(MockMvcRequestBuilders.get(EVENTS_PATH))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Primero"))
                .andExpect(jsonPath("$[1].title").value("Segundo"))
                .andExpect(jsonPath("$[0].status").doesNotExist())
                .andExpect(jsonPath("$[0].createdAt").doesNotExist());
    }

    @Test
    void list_whenNoPublished_returnsEmptyArray() throws Exception {
        culturalEventRepository.save(
                CulturalEvent.builder()
                        .title("Solo borrador")
                        .startsAt(Instant.parse("2030-05-01T12:00:00Z"))
                        .status(EventStatus.DRAFT)
                        .build());

        mockMvc.perform(MockMvcRequestBuilders.get(EVENTS_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getById_published_returnsOkWithoutStatusOrAudit() throws Exception {
        CulturalEvent published =
                culturalEventRepository.save(
                        CulturalEvent.builder()
                                .title("Taller público")
                                .description("Abierto a todos")
                                .startsAt(Instant.parse("2030-09-10T15:00:00Z"))
                                .endsAt(Instant.parse("2030-09-10T17:00:00Z"))
                                .location("Parque")
                                .maxAttendees(40)
                                .status(EventStatus.PUBLISHED)
                                .build());

        mockMvc.perform(MockMvcRequestBuilders.get(EVENTS_PATH + "/" + published.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(published.getId().intValue()))
                .andExpect(jsonPath("$.title").value("Taller público"))
                .andExpect(jsonPath("$.location").value("Parque"))
                .andExpect(jsonPath("$.maxAttendees").value(40))
                .andExpect(jsonPath("$.status").doesNotExist())
                .andExpect(jsonPath("$.createdAt").doesNotExist());
    }

    @Test
    void getById_draft_returnsNotFound() throws Exception {
        CulturalEvent draft =
                culturalEventRepository.save(
                        CulturalEvent.builder()
                                .title("No listado")
                                .startsAt(Instant.parse("2030-04-01T10:00:00Z"))
                                .status(EventStatus.DRAFT)
                                .build());

        mockMvc.perform(MockMvcRequestBuilders.get(EVENTS_PATH + "/" + draft.getId()))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value(ErrorCode.RESOURCE_NOT_FOUND.code()));
    }
}
