package com.almanatura.api.dto;

import java.time.Instant;

import com.almanatura.api.enums.ProjectPillar;
import com.almanatura.api.enums.ProjectStatus;

public record ProjectResponse(
        Long id,
        String title,
        String description,
        ProjectPillar pillar,
        ProjectStatus status,
        Instant startsAt,
        Instant endsAt,
        String location) {}
