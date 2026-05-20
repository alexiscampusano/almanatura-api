package com.almanatura.api.controller;

import jakarta.validation.constraints.Positive;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.almanatura.api.dto.PublicProjectResponse;
import com.almanatura.api.enums.ProjectPillar;
import com.almanatura.api.service.PublicProjectService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/** Catalogue published rural projects and their public activity schedule. */
@RestController
@Validated
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
                    "Returns PUBLISHED projects only, optionally filtered by strategic pillar."
                            + " Paginated.")
    public Page<PublicProjectResponse> list(
            @RequestParam(name = "pillar", required = false) ProjectPillar pillar,
            @ParameterObject @PageableDefault(size = 6) Pageable pageable) {
        return publicProjectService.listPublished(pillar, pageable);
    }

    @GetMapping("/{id}")
    @SecurityRequirements
    @Operation(
            summary = "Get published project by id",
            description = "404 if missing or not PUBLISHED.")
    public PublicProjectResponse getById(@PathVariable @Positive long id) {
        return publicProjectService.getPublished(id);
    }
}
