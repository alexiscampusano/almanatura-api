package com.almanatura.api.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.almanatura.api.dto.PublicProjectResponse;
import com.almanatura.api.enums.ProjectPillar;
import com.almanatura.api.enums.ProjectStatus;
import com.almanatura.api.exception.ResourceNotFoundException;
import com.almanatura.api.mapper.ProjectMapper;
import com.almanatura.api.repository.ProjectRepository;

import lombok.RequiredArgsConstructor;

/** Read models for published projects and their sanitized activity schedule. */
@Service
@RequiredArgsConstructor
public class PublicProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    @Transactional(readOnly = true)
    public Page<PublicProjectResponse> listPublished(ProjectPillar pillar, Pageable pageable) {
        return (pillar == null
                        ? projectRepository.findByStatusOrderByStartsAtAsc(
                                ProjectStatus.PUBLISHED, pageable)
                        : projectRepository.findByStatusAndPillarOrderByStartsAtAsc(
                                ProjectStatus.PUBLISHED, pillar, pageable))
                .map(projectMapper::toPublicResponse);
    }

    @Transactional(readOnly = true)
    public PublicProjectResponse getPublished(long id) {
        return projectRepository
                .findByIdAndStatus(id, ProjectStatus.PUBLISHED)
                .map(projectMapper::toPublicResponse)
                .orElseThrow(() -> ResourceNotFoundException.of("Project", id));
    }
}
