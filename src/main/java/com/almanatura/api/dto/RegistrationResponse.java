package com.almanatura.api.dto;

import java.time.Instant;

/** Safe response after registering for a published event (no DNI or ciphertext). */
public record RegistrationResponse(Long id, Long eventId, Instant registeredAt) {}
