package com.almanatura.api.dto;

import java.time.Instant;

import com.almanatura.api.enums.EventStatus;

/** Safe projection of a {@link com.almanatura.api.entity.CulturalEvent} for admin API responses. */
public record EventResponse(
        Long id,
        String title,
        String description,
        Instant startsAt,
        Instant endsAt,
        String location,
        Integer maxAttendees,
        EventStatus status,
        Instant createdAt,
        Instant updatedAt) {}
