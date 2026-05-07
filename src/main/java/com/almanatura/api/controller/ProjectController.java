package com.almanatura.api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.almanatura.api.dto.PublicProjectActivityResponse;
import com.almanatura.api.dto.PublicProjectResponse;
import com.almanatura.api.enums.ProjectPillar;
import com.almanatura.api.service.PublicProjectService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
@Tag(name = "Projects (public)", description = "Published rural projects. No JWT.")
public class ProjectController {

    private final PublicProjectService publicProjectService;

    @GetMapping
    @SecurityRequirements
    @Operation(
            summary = "List published projects",
            description =
                    "Returns PUBLISHED projects only, optionally filtered by strategic pillar.")
    public List<PublicProjectResponse> list(
            @RequestParam(name = "pillar", required = false) ProjectPillar pillar) {
        return publicProjectService.listPublished(pillar);
    }

    @GetMapping("/{id}/activities")
    @SecurityRequirements
    @Operation(
            summary = "List activities for a published project",
            description =
                    "404 if project is missing or not PUBLISHED. No actor or application PII.")
    public List<PublicProjectActivityResponse> listActivities(@PathVariable long id) {
        return publicProjectService.listPublishedActivities(id);
    }

    @GetMapping("/{id}")
    @SecurityRequirements
    @Operation(
            summary = "Get published project by id",
            description = "404 if missing or not PUBLISHED.")
    public PublicProjectResponse getById(@PathVariable long id) {
        return publicProjectService.getPublished(id);
    }
}
