package com.almanatura.api.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.almanatura.api.dto.PublicActorResponse;
import com.almanatura.api.exception.ResourceNotFoundException;
import com.almanatura.api.repository.ActorRepository;

import lombok.RequiredArgsConstructor;

/** Business logic for admin-facing actor lookups (sorted listings and detail). */
@Service
@RequiredArgsConstructor
public class AdminActorService {

    private final ActorRepository actorRepository;

    @Transactional(readOnly = true)
    public List<PublicActorResponse> findAll() {
        return actorRepository.findAll().stream()
                .sorted((a, b) -> a.getFullName().compareToIgnoreCase(b.getFullName()))
                .map(a -> new PublicActorResponse(a.getId(), a.getFullName(), a.getRegion()))
                .toList();
    }

    @Transactional(readOnly = true)
    public PublicActorResponse getById(long id) {
        return actorRepository
                .findById(id)
                .map(a -> new PublicActorResponse(a.getId(), a.getFullName(), a.getRegion()))
                .orElseThrow(() -> ResourceNotFoundException.of("Actor", id));
    }
}
