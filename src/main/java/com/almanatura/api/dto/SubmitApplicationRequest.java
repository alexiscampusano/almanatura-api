package com.almanatura.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SubmitApplicationRequest(
        @NotNull Long projectId,
        @NotBlank @Size(max = 255) String fullName,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 4, max = 64) String dni,
        @Size(max = 64) String phone) {}
