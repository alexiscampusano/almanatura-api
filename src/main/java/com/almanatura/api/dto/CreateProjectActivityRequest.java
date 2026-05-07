package com.almanatura.api.dto;

import java.time.Instant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.almanatura.api.enums.ProjectActivityStatus;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description =
                "Creates an internal project schedule line (admin only). Timestamps are UTC"
                        + " instants; endsAt may be null.")
public record CreateProjectActivityRequest(
        @NotBlank @Size(max = 255) String title,
        @Size(max = 10_000) String description,
        @NotNull Instant startsAt,
        Instant endsAt,
        @Size(max = 255) String location,
        ProjectActivityStatus status) {}
