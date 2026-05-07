package com.almanatura.api.dto;

import jakarta.validation.constraints.NotNull;

public record InviteActivityParticipationRequest(@NotNull Long actorId) {}
