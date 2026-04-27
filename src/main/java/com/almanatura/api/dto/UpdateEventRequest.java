package com.almanatura.api.dto;

import java.time.Instant;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import com.almanatura.api.enums.EventStatus;

/**
 * Payload for {@code PUT /admin/events/{id}}. Replaces editable fields and lifecycle {@code status}
 * in one request.
 */
public record UpdateEventRequest(
        @NotBlank(message = "title is required")
                @Size(max = 255, message = "title must be at most 255 characters")
                String title,
        @Size(max = 20_000, message = "description must be at most 20000 characters")
                String description,
        @NotNull(message = "startsAt is required") Instant startsAt,
        Instant endsAt,
        @Size(max = 255, message = "location must be at most 255 characters") String location,
        @Positive(message = "maxAttendees must be positive when set") Integer maxAttendees,
        @NotNull(message = "status is required") EventStatus status) {

    @AssertTrue(message = "endsAt must be strictly after startsAt when both are set")
    public boolean isEndAfterStart() {
        if (endsAt == null) {
            return true;
        }
        return endsAt.isAfter(startsAt);
    }
}
