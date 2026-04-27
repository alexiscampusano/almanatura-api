package com.almanatura.api.dto;

import java.time.Instant;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Payload for {@code POST /admin/events}. New events are stored as {@link
 * com.almanatura.api.enums.EventStatus#DRAFT} until published (later task).
 */
public record CreateEventRequest(
        @NotBlank(message = "title is required")
                @Size(max = 255, message = "title must be at most 255 characters")
                String title,
        @Size(max = 20_000, message = "description must be at most 20000 characters")
                String description,
        @NotNull(message = "startsAt is required") Instant startsAt,
        Instant endsAt,
        @Size(max = 255, message = "location must be at most 255 characters") String location,
        @Positive(message = "maxAttendees must be positive when set") Integer maxAttendees) {

    @AssertTrue(message = "endsAt must be strictly after startsAt when both are set")
    public boolean isEndAfterStart() {
        if (endsAt == null) {
            return true;
        }
        return endsAt.isAfter(startsAt);
    }
}
