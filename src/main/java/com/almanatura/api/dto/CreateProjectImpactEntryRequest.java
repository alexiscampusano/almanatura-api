package com.almanatura.api.dto;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateProjectImpactEntryRequest(
        @NotNull @Schema(example = "2030-11-15T12:00:00Z") Instant recordedAt,
        @NotBlank
                @Size(max = 255)
                @Schema(example = "Hogares alcanzados")
                String metricLabel,
        @Schema(example = "120") BigDecimal numericValue,
        @Size(max = 10_000) @Schema(example = "Estimación del trimestre") String notes) {}
