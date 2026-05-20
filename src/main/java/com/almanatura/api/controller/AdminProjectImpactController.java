package com.almanatura.api.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Positive;

import com.almanatura.api.dto.CreateProjectImpactEntryRequest;
import com.almanatura.api.dto.ProjectImpactEntryResponse;
import com.almanatura.api.service.AdminProjectImpactService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/** Admin REST API for lightweight impact metrics on a project. */
@RestController
@Validated
@RequestMapping("/admin/projects/{projectId}/impact-entries")
@RequiredArgsConstructor
@Tag(
        name = "Project impact (admin)",
        description = "Simple impact / follow-up metrics per project. JWT required.")
public class AdminProjectImpactController {

    private final AdminProjectImpactService adminProjectImpactService;

    @GetMapping
    @Operation(summary = "List impact entries for project (newest first)")
    public List<ProjectImpactEntryResponse> list(@PathVariable @Positive long projectId) {
        return adminProjectImpactService.list(projectId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add impact entry")
    public ProjectImpactEntryResponse create(
            @PathVariable @Positive long projectId,
            @Valid @RequestBody CreateProjectImpactEntryRequest body) {
        return adminProjectImpactService.create(projectId, body);
    }
}
