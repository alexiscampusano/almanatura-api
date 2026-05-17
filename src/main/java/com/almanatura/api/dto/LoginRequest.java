package com.almanatura.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.almanatura.api.validation.StrongInternalPassword;

import io.swagger.v3.oas.annotations.media.Schema;

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
                @Schema(example = "admin@almanatura.org")
                String email,
        @NotBlank(message = "password is required")
                @Size(max = 100, message = "password must be at most 100 characters")
                @StrongInternalPassword
                @Schema(example = "MiContraseña1Segura!")
                String password) {}
