package com.almanatura.api.dto;

import jakarta.validation.constraints.NotNull;

import com.almanatura.api.enums.ApplicationStatus;

public record PatchApplicationStatusRequest(@NotNull ApplicationStatus status) {}
