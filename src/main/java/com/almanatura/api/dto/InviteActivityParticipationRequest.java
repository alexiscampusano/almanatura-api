package com.almanatura.api.dto;

import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "References an existing actor id to attach as INVITED on the activity.")
public record InviteActivityParticipationRequest(@NotNull Long actorId) {}
