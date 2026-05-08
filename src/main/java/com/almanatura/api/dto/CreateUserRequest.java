package com.almanatura.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.almanatura.api.enums.Role;
import com.almanatura.api.validation.StrongInternalPassword;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Payload to create an internal user via {@code POST /admin/users}. Password rules match login and
 * bootstrap policy ({@link StrongInternalPassword}).
 */
public record CreateUserRequest(
        @NotBlank(message = "name is required")
                @Size(max = 120, message = "name must be at most 120 characters")
                @Schema(example = "María García López")
                String name,
        @NotBlank(message = "email is required")
                @Email(message = "email must be a well-formed address")
                @Size(max = 180, message = "email must be at most 180 characters")
                @Schema(example = "maria.garcia@almanatura.org")
                String email,
        @NotBlank(message = "password is required")
                @Size(max = 100, message = "password must be at most 100 characters")
                @StrongInternalPassword
                @Schema(example = "CambiarTras1erLogin!")
                String password,
        @NotNull(message = "role is required") @Schema(example = "EVENT_MANAGER") Role role,
        @Schema(example = "true") Boolean enabled) {}
