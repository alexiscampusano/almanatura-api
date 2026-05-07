package com.almanatura.api.dto;

import java.time.Instant;

import com.almanatura.api.enums.ProjectActivityStatus;

public record ProjectActivityResponse(
        Long id,
        Long projectId,
        String title,
        String description,
        Instant startsAt,
        Instant endsAt,
        String location,
        ProjectActivityStatus status) {}
