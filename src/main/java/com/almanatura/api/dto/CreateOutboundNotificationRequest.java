package com.almanatura.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.almanatura.api.enums.NotificationChannel;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateOutboundNotificationRequest(
        @NotNull @Schema(example = "EMAIL") NotificationChannel channel,
        @NotBlank
                @Size(max = 255)
                @Schema(example = "actor@ejemplo.org")
                String recipientHint,
        @Size(max = 500)
                @Schema(example = "Invitación a taller de emprendimiento")
                String subject,
        @Size(max = 10_000)
                @Schema(
                        example =
                                "Estimado/a participante, le informamos que ha sido seleccionado/a"
                                        + " para el próximo taller")
                String body) {}
