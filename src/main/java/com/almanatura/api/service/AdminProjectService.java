package com.almanatura.api.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.almanatura.api.dto.CreateProjectRequest;
import com.almanatura.api.dto.ProjectResponse;
import com.almanatura.api.dto.UpdateProjectRequest;
import com.almanatura.api.entity.Project;
import com.almanatura.api.enums.ProjectStatus;
import com.almanatura.api.exception.ProjectHasApplicationsException;
import com.almanatura.api.exception.ResourceNotFoundException;
import com.almanatura.api.mapper.ProjectMapper;
import com.almanatura.api.repository.ProjectApplicationRepository;
import com.almanatura.api.repository.ProjectRepository;

import lombok.RequiredArgsConstructor;

/** Owns project aggregates for internal CRUD and guarded deletes. */
@Service
@RequiredArgsConstructor
public class AdminProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectApplicationRepository projectApplicationRepository;
    private final ProjectMapper projectMapper;

    @Transactional
    public ProjectResponse create(CreateProjectRequest request) {
        Project entity = projectMapper.toEntity(request);
        entity.setStatus(ProjectStatus.DRAFT);
        return projectMapper.toResponse(projectRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> findAll() {
        return projectRepository.findAllByOrderByStartsAtAsc().stream()
                .map(projectMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProjectResponse getById(long id) {
        return projectRepository
                .findById(id)
                .map(projectMapper::toResponse)
                .orElseThrow(() -> ResourceNotFoundException.of("Project", id));
    }

    @Transactional
    public ProjectResponse update(long id, UpdateProjectRequest request) {
        Project entity =
                projectRepository
                        .findById(id)
                        .orElseThrow(() -> ResourceNotFoundException.of("Project", id));
        projectMapper.updateEntity(request, entity);
        return projectMapper.toResponse(projectRepository.save(entity));
    }

    @Transactional
    public void delete(long id) {
        if (!projectRepository.existsById(id)) {
            throw ResourceNotFoundException.of("Project", id);
        }
        if (projectApplicationRepository.countByProject_Id(id) > 0) {
            throw new ProjectHasApplicationsException(id);
        }
        projectRepository.deleteById(id);
    }
}
