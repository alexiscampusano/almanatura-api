package com.almanatura.api.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.almanatura.api.dto.CreateProjectImpactEntryRequest;
import com.almanatura.api.dto.ProjectImpactEntryResponse;
import com.almanatura.api.entity.Project;
import com.almanatura.api.entity.ProjectImpactEntry;
import com.almanatura.api.exception.ResourceNotFoundException;
import com.almanatura.api.mapper.ProjectActivityMapper;
import com.almanatura.api.repository.ProjectImpactEntryRepository;
import com.almanatura.api.repository.ProjectRepository;

import lombok.RequiredArgsConstructor;

/** Maintains {@link com.almanatura.api.entity.ProjectImpactEntry} timelines per project. */
@Service
@RequiredArgsConstructor
public class AdminProjectImpactService {

    private final ProjectRepository projectRepository;
    private final ProjectImpactEntryRepository projectImpactEntryRepository;
    private final ProjectActivityMapper projectActivityMapper;

    @Transactional(readOnly = true)
    public List<ProjectImpactEntryResponse> list(long projectId) {
        assertProjectExists(projectId);
        return projectImpactEntryRepository
                .findByProject_IdOrderByRecordedAtDesc(projectId)
                .stream()
                .map(projectActivityMapper::toImpactResponse)
                .toList();
    }

    @Transactional
    public ProjectImpactEntryResponse create(
            long projectId, CreateProjectImpactEntryRequest request) {
        Project project =
                projectRepository
                        .findById(projectId)
                        .orElseThrow(() -> ResourceNotFoundException.of("Project", projectId));
        ProjectImpactEntry entity =
                ProjectImpactEntry.builder()
                        .project(project)
                        .recordedAt(request.recordedAt())
                        .metricLabel(request.metricLabel())
                        .numericValue(request.numericValue())
                        .notes(request.notes())
                        .build();
        return projectActivityMapper.toImpactResponse(projectImpactEntryRepository.save(entity));
    }

    private void assertProjectExists(long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw ResourceNotFoundException.of("Project", projectId);
        }
    }
}
