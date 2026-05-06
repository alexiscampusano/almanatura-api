package com.almanatura.api.dto;

import java.time.Instant;

import com.almanatura.api.enums.ProjectPillar;
import com.almanatura.api.enums.ProjectStatus;

/** Per-project application count for admin reporting (no applicant PII). */
public record ProjectApplicationReportRow(
        Long id,
        String title,
        Instant startsAt,
        ProjectPillar pillar,
        ProjectStatus status,
        long applicationCount) {}
