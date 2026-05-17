package com.almanatura.api.dto;

import java.time.LocalDate;

import com.almanatura.api.enums.ProjectPillar;

public record PublicProjectResponse(
        Long id,
        String title,
        String description,
        ProjectPillar pillar,
        LocalDate startsAt,
        LocalDate endsAt,
        String location,
        String imageUrl) {}
