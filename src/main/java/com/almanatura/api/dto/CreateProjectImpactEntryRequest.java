package com.almanatura.api.dto;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateProjectImpactEntryRequest(
        @NotNull Instant recordedAt,
        @NotBlank @Size(max = 255) String metricLabel,
        BigDecimal numericValue,
        @Size(max = 10_000) String notes) {}
