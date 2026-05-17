package com.almanatura.api.dto;

import java.time.LocalDate;

import com.almanatura.api.enums.ProjectPillar;
import com.almanatura.api.enums.ProjectStatus;

public record ProjectResponse(
        Long id,
        String title,
        String description,
        ProjectPillar pillar,
        ProjectStatus status,
        LocalDate startsAt,
        LocalDate endsAt,
        String location,
        String imageUrl) {}
