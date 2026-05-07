package com.almanatura.api.dto;

import java.time.Instant;

import com.almanatura.api.enums.ProjectActivityStatus;

/** Public schedule line for a published project (no actor or application PII). */
public record PublicProjectActivityResponse(
        Long id,
        String title,
        String description,
        Instant startsAt,
        Instant endsAt,
        String location,
        ProjectActivityStatus status) {}
