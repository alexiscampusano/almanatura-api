package com.almanatura.api.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.almanatura.api.dto.AdminApplicationResponse;
import com.almanatura.api.dto.PatchApplicationStatusRequest;
import com.almanatura.api.entity.Actor;
import com.almanatura.api.entity.ProjectApplication;
import com.almanatura.api.enums.ApplicationStatus;
import com.almanatura.api.exception.ResourceNotFoundException;
import com.almanatura.api.repository.ActorRepository;
import com.almanatura.api.repository.ProjectApplicationRepository;
import com.almanatura.api.util.DniCipherService;

import lombok.RequiredArgsConstructor;

/** Staff workflows over applications including decryption of national IDs for authorized reads. */
@Service
@RequiredArgsConstructor
public class AdminApplicationService {

    private final ProjectApplicationRepository projectApplicationRepository;
    private final ActorRepository actorRepository;
    private final DniCipherService dniCipherService;

    @Transactional(readOnly = true)
    public List<AdminApplicationResponse> search(Long projectId, ApplicationStatus status) {
        return projectApplicationRepository.search(projectId, status).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AdminApplicationResponse getById(long id) {
        return projectApplicationRepository
                .findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> ResourceNotFoundException.of("ProjectApplication", id));
    }

    @Transactional
    public AdminApplicationResponse patchStatus(long id, PatchApplicationStatusRequest body) {
        ProjectApplication app =
                projectApplicationRepository
                        .findById(id)
                        .orElseThrow(() -> ResourceNotFoundException.of("ProjectApplication", id));

        ApplicationStatus from = app.getStatus();
        ApplicationStatus to = body.status();
        if (from == to) {
            return toResponse(app);
        }
        ApplicationStatusTransitions.requireTransition(from, to);

        if (to == ApplicationStatus.REGISTERED_AS_ACTOR) {
            Actor actor = Actor.builder()
                    .fullName(app.getFullName())
                    .region(null)
                    .email(app.getEmail())
                    .phone(app.getPhone())
                    .dniEncrypted(app.getDniEncrypted())
                    .build();
            app.setActor(actorRepository.save(actor));
        }

        app.setStatus(to);
        return toResponse(projectApplicationRepository.save(app));
    }

    private AdminApplicationResponse toResponse(ProjectApplication entity) {
        Long actorId = entity.getActor() == null ? null : entity.getActor().getId();
        return new AdminApplicationResponse(
                entity.getId(),
                entity.getProject().getId(),
                actorId,
                entity.getStatus(),
                entity.getFullName(),
                entity.getEmail(),
                entity.getPhone(),
                dniCipherService.decrypt(entity.getDniEncrypted()),
                entity.getCreatedAt());
    }
}
