package com.almanatura.api.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.almanatura.api.dto.PublicProjectActivityResponse;
import com.almanatura.api.dto.PublicProjectResponse;
import com.almanatura.api.enums.ProjectPillar;
import com.almanatura.api.enums.ProjectStatus;
import com.almanatura.api.exception.ResourceNotFoundException;
import com.almanatura.api.mapper.ProjectActivityMapper;
import com.almanatura.api.mapper.ProjectMapper;
import com.almanatura.api.repository.ProjectActivityRepository;
import com.almanatura.api.repository.ProjectRepository;

import lombok.RequiredArgsConstructor;

/** Read models for published projects and their sanitized activity schedule. */
@Service
@RequiredArgsConstructor
public class PublicProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectActivityRepository projectActivityRepository;
    private final ProjectMapper projectMapper;
    private final ProjectActivityMapper projectActivityMapper;

    @Transactional(readOnly = true)
    public List<PublicProjectResponse> listPublished(ProjectPillar pillar) {
        return (pillar == null
                        ? projectRepository.findByStatusOrderByStartsAtAsc(ProjectStatus.PUBLISHED)
                        : projectRepository.findByStatusAndPillarOrderByStartsAtAsc(
                                ProjectStatus.PUBLISHED, pillar))
                .stream().map(projectMapper::toPublicResponse).toList();
    }

    @Transactional(readOnly = true)
    public PublicProjectResponse getPublished(long id) {
        return projectRepository
                .findByIdAndStatus(id, ProjectStatus.PUBLISHED)
                .map(projectMapper::toPublicResponse)
                .orElseThrow(() -> ResourceNotFoundException.of("Project", id));
    }

    @Transactional(readOnly = true)
    public List<PublicProjectActivityResponse> listPublishedActivities(long projectId) {
        if (!projectRepository.existsByIdAndStatus(projectId, ProjectStatus.PUBLISHED)) {
            throw ResourceNotFoundException.of("Project", projectId);
        }
        return projectActivityRepository.findByProject_IdOrderByStartsAtAsc(projectId).stream()
                .map(projectActivityMapper::toPublicResponse)
                .toList();
    }
}
