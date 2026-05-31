package com.almanatura.api.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.almanatura.api.dto.PublicActorResponse;
import com.almanatura.api.dto.PublicActorResponse.ActorProjectInfo;
import com.almanatura.api.enums.ProjectPillar;
import com.almanatura.api.exception.ResourceNotFoundException;
import com.almanatura.api.repository.ActorRepository;
import com.almanatura.api.repository.ProjectApplicationRepository;
import com.almanatura.api.util.DniCipherService;

import lombok.RequiredArgsConstructor;

/** Business logic for admin-facing actor lookups (sorted listings and detail). */
@Service
@RequiredArgsConstructor
public class AdminActorService {

    private final ActorRepository actorRepository;
    private final ProjectApplicationRepository applicationRepository;
    private final DniCipherService dniCipherService;

    @Transactional(readOnly = true)
    public List<PublicActorResponse> findAll(ProjectPillar pillar) {
        List<com.almanatura.api.entity.Actor> actors;
        if (pillar != null) {
            actors = actorRepository.findByProjectPillar(pillar);
        } else {
            actors = actorRepository.findAll();
        }
        return actors.stream()
                .sorted((a, b) -> a.getFullName().compareToIgnoreCase(b.getFullName()))
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PublicActorResponse getById(long id) {
        return actorRepository
                .findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> ResourceNotFoundException.of("Actor", id));
    }

    private PublicActorResponse toResponse(com.almanatura.api.entity.Actor actor) {
        List<ActorProjectInfo> projects =
                applicationRepository.findByActorIdOrderByCreatedAtDesc(actor.getId()).stream()
                        .map(
                                app ->
                                        new ActorProjectInfo(
                                                app.getProject().getId(),
                                                app.getProject().getTitle(),
                                                app.getProject().getPillar().name(),
                                                app.getStatus().name()))
                        .toList();

        String nationalId =
                actor.getDniEncrypted() != null
                        ? dniCipherService.decrypt(actor.getDniEncrypted())
                        : null;

        return new PublicActorResponse(
                actor.getId(),
                actor.getFullName(),
                actor.getRegion(),
                actor.getEmail(),
                actor.getPhone(),
                nationalId,
                projects);
    }
}
