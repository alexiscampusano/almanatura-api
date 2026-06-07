package com.almanatura.api.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import com.almanatura.api.entity.User;
import com.almanatura.api.enums.Role;
import com.almanatura.api.exception.ErrorCode;
import com.almanatura.api.repository.ActorRepository;
import com.almanatura.api.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminActorControllerTest {

    private static final String ACTORS_PATH = "/admin/actors";
    private static final String PROBLEM_JSON = MediaType.APPLICATION_PROBLEM_JSON_VALUE;

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private ActorRepository actorRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private User eventManager;

    @BeforeEach
    void setUp() {
        actorRepository.deleteAll();
        userRepository.deleteAll();
        eventManager =
                userRepository.save(
                        User.builder()
                                .name("Eve Actors")
                                .email("eve.actors@almanatura.org")
                                .passwordHash(passwordEncoder.encode("EveActors9!Z"))
                                .role(Role.EVENT_MANAGER)
                                .enabled(true)
                                .build());
        actorRepository.save(
                Actor.builder()
                        .fullName("Carlos Ruiz")
                        .email("carlos@example.org")
                        .build());
        actorRepository.save(
                Actor.builder()
                        .fullName("Maria Lopez")
                        .email("maria@example.org")
                        .build());
    }

    @Test
    void list_withoutAuth_returnsAuthenticationRequired() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(ACTORS_PATH))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value(ErrorCode.AUTHENTICATION_REQUIRED.code()));
    }

    @Test
    void list_asEventManager_returnsAllActors() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get(ACTORS_PATH)
                                .with(user(eventManager)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].fullName").value("Carlos Ruiz"));
    }

    @Test
    void getById_existingActor_returnsActor() throws Exception {
        var actors = actorRepository.findAll();
        long id = actors.getFirst().getId();

        mockMvc.perform(
                        MockMvcRequestBuilders.get(ACTORS_PATH + "/" + id)
                                .with(user(eventManager)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Carlos Ruiz"));
    }

    @Test
    void getById_notFound_returnsResourceNotFound() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get(ACTORS_PATH + "/99999")
                                .with(user(eventManager)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value(ErrorCode.RESOURCE_NOT_FOUND.code()));
    }
}
