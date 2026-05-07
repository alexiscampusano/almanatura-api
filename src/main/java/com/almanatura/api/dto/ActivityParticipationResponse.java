package com.almanatura.api.dto;

import com.almanatura.api.enums.ActivityParticipationStatus;

public record ActivityParticipationResponse(
        Long id, Long activityId, Long actorId, ActivityParticipationStatus status) {}
