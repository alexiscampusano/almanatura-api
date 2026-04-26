package com.almanatura.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Credentials submitted by an internal user (super_user / event_manager) to obtain a JWT.
 *
 * <p>Length bounds mirror the entity columns so an oversize payload is rejected before reaching
 * persistence: email is {@code 180} chars, password is bounded at {@code 100} chars to fit the
 * BCrypt hash column.
 */
public record LoginRequest(
        @NotBlank(message = "email is required")
                @Email(message = "email must be a well-formed address")
                @Size(max = 180, message = "email must be at most 180 characters")
                String email,
        @NotBlank(message = "password is required")
                @Size(min = 8, max = 100, message = "password must be 8-100 characters")
                String password) {}
