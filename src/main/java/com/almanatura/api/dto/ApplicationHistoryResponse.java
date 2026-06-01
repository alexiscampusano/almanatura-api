package com.almanatura.api.dto;

import java.time.Instant;

import com.almanatura.api.enums.ApplicationStatus;

public record ApplicationHistoryResponse(
        Long id,
        ApplicationStatus oldStatus,
        ApplicationStatus newStatus,
        String changedBy,
        String notes,
        Instant changedAt) {}
