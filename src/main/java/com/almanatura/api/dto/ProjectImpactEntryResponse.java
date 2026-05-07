package com.almanatura.api.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record ProjectImpactEntryResponse(
        Long id,
        Long projectId,
        Instant recordedAt,
        String metricLabel,
        BigDecimal numericValue,
        String notes) {}
