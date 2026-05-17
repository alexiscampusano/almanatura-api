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

import com.almanatura.api.dto.CreateProjectRequest;
import com.almanatura.api.dto.ProjectResponse;
import com.almanatura.api.dto.UpdateProjectRequest;
import com.almanatura.api.service.AdminProjectService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/** Admin REST API for project lifecycle (create, read, update, delete). */
@RestController
@RequestMapping("/admin/projects")
@RequiredArgsConstructor
@Tag(
        name = "Projects (admin)",
        description = "Project CRUD. Requires internal JWT (super_user or event_manager).")
public class AdminProjectController {

    private final AdminProjectService adminProjectService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create project", description = "New projects start in DRAFT status.")
    public ProjectResponse create(@Valid @RequestBody CreateProjectRequest body) {
        return adminProjectService.create(body);
    }

    @GetMapping
    @Operation(summary = "List projects")
    public List<ProjectResponse> list() {
        return adminProjectService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get project by id")
    public ProjectResponse getById(@PathVariable long id) {
        return adminProjectService.getById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Replace project fields and status")
    public ProjectResponse update(
            @PathVariable long id, @Valid @RequestBody UpdateProjectRequest body) {
        return adminProjectService.update(id, body);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete project",
            description = "409 if any applications still reference the project.")
    public void delete(@PathVariable long id) {
        adminProjectService.delete(id);
    }
}
