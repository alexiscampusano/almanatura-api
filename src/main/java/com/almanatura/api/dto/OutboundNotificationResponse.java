package com.almanatura.api.dto;

import com.almanatura.api.enums.NotificationChannel;
import com.almanatura.api.enums.OutboundNotificationStatus;

public record OutboundNotificationResponse(
        Long id,
        NotificationChannel channel,
        String recipientHint,
        String subject,
        String body,
        OutboundNotificationStatus status) {}
