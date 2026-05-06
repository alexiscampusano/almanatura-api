package com.almanatura.api.dto;

import com.almanatura.api.enums.ApplicationStatus;

import jakarta.validation.constraints.NotNull;

public record PatchApplicationStatusRequest(@NotNull ApplicationStatus status) {}
