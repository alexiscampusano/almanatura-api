package com.almanatura.api.dto;

import java.time.Instant;

/** Admin-only projection including decrypted national ID for foundation staff. */
public record AdminAttendeeResponse(
        Long id,
        Long eventId,
        String fullName,
        String email,
        String phone,
        String dni,
        Instant registeredAt) {}
