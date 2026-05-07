package com.almanatura.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.almanatura.api.enums.NotificationChannel;

public record CreateOutboundNotificationRequest(
        @NotNull NotificationChannel channel,
        @NotBlank @Size(max = 255) String recipientHint,
        @Size(max = 500) String subject,
        @Size(max = 10_000) String body) {}
