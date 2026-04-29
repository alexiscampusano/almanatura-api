package com.almanatura.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Payload for {@code POST /events/{id}/register}. */
public record RegisterAttendeeRequest(
        @NotBlank(message = "fullName is required")
                @Size(max = 255, message = "fullName must be at most 255 characters")
                String fullName,
        @NotBlank(message = "email is required")
                @Email(message = "email must be a valid address")
                @Size(max = 255, message = "email must be at most 255 characters")
                String email,
        @NotBlank(message = "dni is required")
                @Size(max = 32, message = "dni must be at most 32 characters")
                String dni,
        @Size(max = 64, message = "phone must be at most 64 characters") String phone) {}
