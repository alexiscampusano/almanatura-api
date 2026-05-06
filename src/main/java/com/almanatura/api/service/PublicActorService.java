package com.almanatura.api.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.almanatura.api.dto.PublicActorResponse;
import com.almanatura.api.enums.ApplicationStatus;
import com.almanatura.api.enums.ProjectPillar;
import com.almanatura.api.enums.ProjectStatus;
import com.almanatura.api.repository.ActorRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PublicActorService {

    private final ActorRepository actorRepository;

    @Transactional(readOnly = true)
    public List<PublicActorResponse> listDirectory(ProjectPillar pillar) {
        return actorRepository
                .findDirectoryActors(
                        ApplicationStatus.REGISTERED_AS_ACTOR,
                        ProjectStatus.PUBLISHED,
                        pillar)
                .stream()
                .map(a -> new PublicActorResponse(a.getId(), a.getFullName(), a.getRegion()))
                .toList();
    }
}
