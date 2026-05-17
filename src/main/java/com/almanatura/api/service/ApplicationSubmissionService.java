package com.almanatura.api.service;

import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.almanatura.api.dto.ApplicationSubmittedResponse;
import com.almanatura.api.dto.SubmitApplicationRequest;
import com.almanatura.api.entity.Project;
import com.almanatura.api.entity.ProjectApplication;
import com.almanatura.api.enums.ApplicationStatus;
import com.almanatura.api.enums.ProjectStatus;
import com.almanatura.api.exception.ApplicationAlreadyExistsException;
import com.almanatura.api.exception.ResourceNotFoundException;
import com.almanatura.api.repository.ProjectApplicationRepository;
import com.almanatura.api.repository.ProjectRepository;
import com.almanatura.api.util.DniCipherService;

import lombok.RequiredArgsConstructor;

/** Validates uniqueness and encrypts applicant payloads before persistence. */
@Service
@RequiredArgsConstructor
public class ApplicationSubmissionService {

    private final ProjectRepository projectRepository;
    private final ProjectApplicationRepository projectApplicationRepository;
    private final DniCipherService dniCipherService;

    @Transactional
    public ApplicationSubmittedResponse submit(SubmitApplicationRequest request) {
        Project project =
                projectRepository
                        .findByIdAndStatus(request.projectId(), ProjectStatus.PUBLISHED)
                        .orElseThrow(
                                () -> ResourceNotFoundException.of("Project", request.projectId()));

        String emailNormalized = request.email().trim().toLowerCase(Locale.ROOT);
        if (projectApplicationRepository.existsByProject_IdAndEmailIgnoreCase(
                project.getId(), emailNormalized)) {
            throw new ApplicationAlreadyExistsException();
        }

        ProjectApplication entity =
                ProjectApplication.builder()
                        .project(project)
                        .status(ApplicationStatus.SUBMITTED)
                        .fullName(request.fullName().trim())
                        .email(emailNormalized)
                        .phone(
                                request.phone() == null || request.phone().isBlank()
                                        ? null
                                        : request.phone().trim())
                        .dniEncrypted(dniCipherService.encrypt(request.dni().trim()))
                        .build();

        ProjectApplication saved = projectApplicationRepository.save(entity);
        return new ApplicationSubmittedResponse(
                saved.getId(), project.getId(), saved.getCreatedAt());
    }
}
