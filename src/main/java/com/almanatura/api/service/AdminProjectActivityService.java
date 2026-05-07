package com.almanatura.api.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.almanatura.api.dto.CreateProjectActivityRequest;
import com.almanatura.api.dto.ProjectActivityResponse;
import com.almanatura.api.dto.UpdateProjectActivityRequest;
import com.almanatura.api.entity.Project;
import com.almanatura.api.entity.ProjectActivity;
import com.almanatura.api.exception.ResourceNotFoundException;
import com.almanatura.api.mapper.ProjectActivityMapper;
import com.almanatura.api.repository.ProjectActivityRepository;
import com.almanatura.api.repository.ProjectRepository;

import lombok.RequiredArgsConstructor;

/** Mutates {@link com.almanatura.api.entity.ProjectActivity} rows scoped under owning projects. */
@Service
@RequiredArgsConstructor
public class AdminProjectActivityService {

    private final ProjectRepository projectRepository;
    private final ProjectActivityRepository projectActivityRepository;
    private final ProjectActivityMapper projectActivityMapper;

    @Transactional
    public ProjectActivityResponse create(long projectId, CreateProjectActivityRequest request) {
        Project project =
                projectRepository
                        .findById(projectId)
                        .orElseThrow(() -> ResourceNotFoundException.of("Project", projectId));
        ProjectActivity entity = projectActivityMapper.toEntity(request);
        entity.setProject(project);
        return projectActivityMapper.toResponse(projectActivityRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<ProjectActivityResponse> list(long projectId) {
        assertProjectExists(projectId);
        return projectActivityRepository.findByProject_IdOrderByStartsAtAsc(projectId).stream()
                .map(projectActivityMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProjectActivityResponse getById(long projectId, long activityId) {
        return projectActivityRepository
                .findByIdAndProject_Id(activityId, projectId)
                .map(projectActivityMapper::toResponse)
                .orElseThrow(() -> ResourceNotFoundException.of("ProjectActivity", activityId));
    }

    @Transactional
    public ProjectActivityResponse update(
            long projectId, long activityId, UpdateProjectActivityRequest request) {
        ProjectActivity entity =
                projectActivityRepository
                        .findByIdAndProject_Id(activityId, projectId)
                        .orElseThrow(
                                () -> ResourceNotFoundException.of("ProjectActivity", activityId));
        projectActivityMapper.updateEntity(request, entity);
        return projectActivityMapper.toResponse(projectActivityRepository.save(entity));
    }

    @Transactional
    public void delete(long projectId, long activityId) {
        ProjectActivity entity =
                projectActivityRepository
                        .findByIdAndProject_Id(activityId, projectId)
                        .orElseThrow(
                                () -> ResourceNotFoundException.of("ProjectActivity", activityId));
        projectActivityRepository.delete(entity);
    }

    private void assertProjectExists(long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw ResourceNotFoundException.of("Project", projectId);
        }
    }
}
