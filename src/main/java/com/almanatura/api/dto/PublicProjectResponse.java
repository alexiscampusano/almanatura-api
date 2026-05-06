package com.almanatura.api.dto;

import java.time.Instant;

import com.almanatura.api.enums.ProjectPillar;

public record PublicProjectResponse(
        Long id,
        String title,
        String description,
        ProjectPillar pillar,
        Instant startsAt,
        Instant endsAt,
        String location) {}
