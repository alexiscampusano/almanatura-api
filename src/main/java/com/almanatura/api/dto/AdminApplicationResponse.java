package com.almanatura.api.dto;

import java.time.Instant;

import com.almanatura.api.enums.ApplicationStatus;

public record AdminApplicationResponse(
        Long id,
        Long projectId,
        Long actorId,
        ApplicationStatus status,
        String fullName,
        String email,
        String phone,
        String nationalId,
        Instant createdAt) {}
