package com.almanatura.api.dto;

import java.time.Instant;

/** Public projection of a cultural event (published agenda); excludes status and audit fields. */
public record PublicEventResponse(
        Long id,
        String title,
        String description,
        Instant startsAt,
        Instant endsAt,
        String location,
        Integer maxAttendees) {}
