package com.almanatura.api.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.almanatura.api.dto.CreateProjectActivityRequest;
import com.almanatura.api.dto.ProjectActivityResponse;
import com.almanatura.api.dto.UpdateProjectActivityRequest;
import com.almanatura.api.service.AdminProjectActivityService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/projects/{projectId}/activities")
@RequiredArgsConstructor
@Tag(
        name = "Project activities (admin)",
        description =
                "CRUD for project schedule items. Requires internal JWT (super_user or"
                        + " event_manager).")
public class AdminProjectActivityController {

    private final AdminProjectActivityService adminProjectActivityService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create activity for project")
    public ProjectActivityResponse create(
            @PathVariable long projectId, @Valid @RequestBody CreateProjectActivityRequest body) {
        return adminProjectActivityService.create(projectId, body);
    }

    @GetMapping
    @Operation(summary = "List activities for project")
    public List<ProjectActivityResponse> list(@PathVariable long projectId) {
        return adminProjectActivityService.list(projectId);
    }

    @GetMapping("/{activityId}")
    @Operation(summary = "Get activity by id (scoped to project)")
    public ProjectActivityResponse getById(
            @PathVariable long projectId, @PathVariable long activityId) {
        return adminProjectActivityService.getById(projectId, activityId);
    }

    @PutMapping("/{activityId}")
    @Operation(summary = "Replace activity fields")
    public ProjectActivityResponse update(
            @PathVariable long projectId,
            @PathVariable long activityId,
            @Valid @RequestBody UpdateProjectActivityRequest body) {
        return adminProjectActivityService.update(projectId, activityId, body);
    }

    @DeleteMapping("/{activityId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete activity")
    public void delete(@PathVariable long projectId, @PathVariable long activityId) {
        adminProjectActivityService.delete(projectId, activityId);
    }
}
