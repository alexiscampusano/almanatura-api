package com.almanatura.api.dto;

import com.almanatura.api.enums.ProjectPillar;
import com.almanatura.api.enums.ProjectStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record UpdateProjectRequest(
        @NotBlank @Size(max = 255) String title,
        @Size(max = 10_000) String description,
        @NotNull ProjectPillar pillar,
        @NotNull ProjectStatus status,
        Instant startsAt,
        Instant endsAt,
        @Size(max = 255) String location) {}
