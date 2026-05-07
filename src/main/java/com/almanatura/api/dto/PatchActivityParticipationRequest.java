package com.almanatura.api.dto;

import jakarta.validation.constraints.NotNull;

import com.almanatura.api.enums.ActivityParticipationStatus;

public record PatchActivityParticipationRequest(@NotNull ActivityParticipationStatus status) {}
